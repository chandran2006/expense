package com.finmate.ai.service;

import com.finmate.ai.dto.ReceiptDTO;
import com.finmate.ai.dto.TransactionRequest;
import com.finmate.ai.entity.Receipt;
import com.finmate.ai.entity.TransactionType;
import com.finmate.ai.entity.User;
import com.finmate.ai.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptService {
    
    private final ReceiptRepository receiptRepository;
    private final UserService userService;
    private final TransactionService transactionService;
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @Value("${tesseract.data-path}")
    private String tesseractDataPath;
    
    public ReceiptDTO uploadAndProcessReceipt(MultipartFile file) {
        try {
            User user = userService.getCurrentUser();
            
            // Create upload directory if not exists
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // Save file
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            // Perform OCR
            String extractedText = performOCR(filePath.toFile());
            
            // Extract data
            Double amount = extractAmount(extractedText);
            LocalDate date = extractDate(extractedText);
            String merchantName = extractMerchantName(extractedText);
            
            // Save receipt
            Receipt receipt = new Receipt();
            receipt.setUser(user);
            receipt.setImagePath(filePath.toString());
            receipt.setExtractedAmount(amount);
            receipt.setExtractedDate(date);
            receipt.setMerchantName(merchantName);
            receiptRepository.save(receipt);
            
            // Auto-create transaction if amount extracted
            if (amount != null && amount > 0) {
                TransactionRequest transactionRequest = new TransactionRequest();
                transactionRequest.setType(TransactionType.EXPENSE);
                transactionRequest.setCategory(merchantName != null ? merchantName : "Receipt");
                transactionRequest.setAmount(amount);
                transactionRequest.setDescription("Auto-created from receipt");
                transactionRequest.setDate(date != null ? date : LocalDate.now());
                
                transactionService.addTransaction(transactionRequest);
            }
            
            return mapToDTO(receipt);
            
        } catch (Exception e) {
            log.error("Error processing receipt: ", e);
            throw new RuntimeException("Failed to process receipt: " + e.getMessage());
        }
    }
    
    public List<ReceiptDTO> getAllReceipts() {
        User user = userService.getCurrentUser();
        return receiptRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    private String performOCR(File imageFile) {
        try {
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(tesseractDataPath);
            tesseract.setLanguage("eng");
            return tesseract.doOCR(imageFile);
        } catch (Exception e) {
            log.error("OCR failed: ", e);
            return "";
        }
    }
    
    private Double extractAmount(String text) {
        // Pattern for currency: ₹1234.56 or Rs.1234.56 or 1234.56
        Pattern pattern = Pattern.compile("(?:₹|Rs\\.?|INR)?\\s*(\\d+(?:,\\d{3})*(?:\\.\\d{2})?)");
        Matcher matcher = pattern.matcher(text);
        
        double maxAmount = 0.0;
        while (matcher.find()) {
            try {
                String amountStr = matcher.group(1).replace(",", "");
                double amount = Double.parseDouble(amountStr);
                if (amount > maxAmount) {
                    maxAmount = amount;
                }
            } catch (NumberFormatException e) {
                log.warn("Failed to parse amount: " + matcher.group(1));
            }
        }
        
        return maxAmount > 0 ? maxAmount : null;
    }
    
    private LocalDate extractDate(String text) {
        // Common date patterns
        String[] patterns = {
            "\\d{2}/\\d{2}/\\d{4}",
            "\\d{2}-\\d{2}-\\d{4}",
            "\\d{4}-\\d{2}-\\d{2}"
        };
        
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        };
        
        for (int i = 0; i < patterns.length; i++) {
            Pattern pattern = Pattern.compile(patterns[i]);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                try {
                    return LocalDate.parse(matcher.group(), formatters[i]);
                } catch (Exception e) {
                    log.warn("Failed to parse date: " + matcher.group());
                }
            }
        }
        
        return null;
    }
    
    private String extractMerchantName(String text) {
        // Extract first line with significant text (usually merchant name)
        String[] lines = text.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.length() > 3 && line.matches(".*[a-zA-Z]{3,}.*")) {
                return line.substring(0, Math.min(line.length(), 50));
            }
        }
        return null;
    }
    
    private ReceiptDTO mapToDTO(Receipt receipt) {
        return new ReceiptDTO(
                receipt.getId(),
                receipt.getImagePath(),
                receipt.getExtractedAmount(),
                receipt.getExtractedDate(),
                receipt.getMerchantName(),
                receipt.getCreatedAt()
        );
    }
}
