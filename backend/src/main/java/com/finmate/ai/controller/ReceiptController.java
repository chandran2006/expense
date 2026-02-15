package com.finmate.ai.controller;

import com.finmate.ai.dto.ApiResponse;
import com.finmate.ai.dto.ReceiptDTO;
import com.finmate.ai.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Receipt", description = "Receipt OCR scanning APIs")
public class ReceiptController {
    
    private final ReceiptService receiptService;
    
    @PostMapping("/upload")
    @Operation(summary = "Upload and process receipt with OCR")
    public ResponseEntity<ApiResponse<ReceiptDTO>> uploadReceipt(@RequestParam("file") MultipartFile file) {
        ReceiptDTO receipt = receiptService.uploadAndProcessReceipt(file);
        return ResponseEntity.ok(ApiResponse.success("Receipt processed successfully", receipt));
    }
    
    @GetMapping
    @Operation(summary = "Get all receipts")
    public ResponseEntity<ApiResponse<List<ReceiptDTO>>> getAllReceipts() {
        List<ReceiptDTO> receipts = receiptService.getAllReceipts();
        return ResponseEntity.ok(ApiResponse.success("Receipts retrieved successfully", receipts));
    }
}
