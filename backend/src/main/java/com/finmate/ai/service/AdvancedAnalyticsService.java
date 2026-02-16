package com.finmate.ai.service;

import com.finmate.ai.entity.Transaction;
import com.finmate.ai.entity.User;
import com.finmate.ai.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvancedAnalyticsService {
    
    private final TransactionRepository transactionRepository;
    
    @Cacheable(value = "analytics", key = "#userId + '-spending-pattern'")
    public Map<String, Object> getSpendingPattern(Long userId) {
        User user = new User();
        user.setId(userId);
        List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user);
        
        Map<String, Double> categorySpending = transactions.stream()
            .filter(t -> "EXPENSE".equals(t.getType().name()))
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ));
        
        String topCategory = categorySpending.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
        
        double avgDailySpending = transactions.stream()
            .filter(t -> "EXPENSE".equals(t.getType().name()))
            .mapToDouble(Transaction::getAmount)
            .average()
            .orElse(0.0);
        
        Map<String, Object> result = new HashMap<>();
        result.put("categorySpending", categorySpending);
        result.put("topCategory", topCategory);
        result.put("avgDailySpending", avgDailySpending);
        result.put("totalTransactions", transactions.size());
        
        return result;
    }
    
    @Cacheable(value = "analytics", key = "#userId + '-forecast'")
    public Map<String, Object> getForecast(Long userId) {
        User user = new User();
        user.setId(userId);
        List<Transaction> last30Days = transactionRepository.findByUserAndDateBetween(
            user,
            LocalDate.now().minusDays(30),
            LocalDate.now()
        );
        
        double avgExpense = last30Days.stream()
            .filter(t -> "EXPENSE".equals(t.getType().name()))
            .mapToDouble(Transaction::getAmount)
            .average()
            .orElse(0.0);
        
        double projectedMonthly = avgExpense * 30;
        
        Map<String, Object> result = new HashMap<>();
        result.put("projectedMonthlyExpense", projectedMonthly);
        result.put("basedOnDays", 30);
        result.put("avgDailyExpense", avgExpense);
        
        return result;
    }
}
