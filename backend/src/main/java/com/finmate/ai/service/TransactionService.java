package com.finmate.ai.service;

import com.finmate.ai.dto.MonthlySummaryDTO;
import com.finmate.ai.dto.TransactionDTO;
import com.finmate.ai.dto.TransactionRequest;
import com.finmate.ai.entity.Transaction;
import com.finmate.ai.entity.TransactionType;
import com.finmate.ai.entity.User;
import com.finmate.ai.exception.ResourceNotFoundException;
import com.finmate.ai.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final DailySpendingService dailySpendingService;
    private final BudgetService budgetService;
    
    public TransactionDTO addTransaction(TransactionRequest request) {
        User user = userService.getCurrentUser();
        
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        
        transactionRepository.save(transaction);
        
        // Check daily and monthly limits after adding expense
        if (transaction.getType() == TransactionType.EXPENSE) {
            dailySpendingService.checkDailyLimit(user);
            budgetService.checkMonthlyBudget(user);
        }
        
        return mapToDTO(transaction);
    }
    
    public TransactionDTO updateTransaction(Long id, TransactionRequest request) {
        User user = userService.getCurrentUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Transaction not found");
        }
        
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        
        transactionRepository.save(transaction);
        return mapToDTO(transaction);
    }
    
    public void deleteTransaction(Long id) {
        User user = userService.getCurrentUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Transaction not found");
        }
        
        transactionRepository.delete(transaction);
    }
    
    public List<TransactionDTO> getAllTransactions() {
        User user = userService.getCurrentUser();
        return transactionRepository.findByUserOrderByDateDesc(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public MonthlySummaryDTO getMonthlySummary(String month) {
        User user = userService.getCurrentUser();
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        Double totalIncome = transactionRepository.sumByUserAndTypeAndDateBetween(
                user, TransactionType.INCOME, startDate, endDate);
        Double totalExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                user, TransactionType.EXPENSE, startDate, endDate);
        
        totalIncome = totalIncome != null ? totalIncome : 0.0;
        totalExpense = totalExpense != null ? totalExpense : 0.0;
        
        Map<String, Double> categoryWiseExpense = getCategoryWiseExpense(user, startDate, endDate);
        
        return new MonthlySummaryDTO(
                totalIncome,
                totalExpense,
                totalIncome - totalExpense,
                categoryWiseExpense
        );
    }
    
    public Map<String, Double> getCategoryWiseExpense(User user, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = transactionRepository.sumByCategoryAndUserAndTypeAndDateBetween(
                user, TransactionType.EXPENSE, startDate, endDate);
        
        Map<String, Double> categoryMap = new HashMap<>();
        for (Object[] result : results) {
            categoryMap.put((String) result[0], (Double) result[1]);
        }
        return categoryMap;
    }
    
    private TransactionDTO mapToDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate()
        );
    }
}
