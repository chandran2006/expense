package com.finmate.ai.service;

import com.finmate.ai.entity.Transaction;
import com.finmate.ai.entity.User;
import com.finmate.ai.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {
    
    private final TransactionRepository transactionRepository;
    
    public String exportToCSV(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = new User();
        user.setId(userId);
        List<Transaction> transactions = transactionRepository.findByUserAndDateBetween(user, startDate, endDate);
        
        StringBuilder csv = new StringBuilder();
        csv.append("Date,Type,Category,Amount,Description\n");
        
        transactions.forEach(t -> csv.append(String.format("%s,%s,%s,%.2f,%s\n",
            t.getDate(),
            t.getType(),
            t.getCategory(),
            t.getAmount(),
            t.getDescription() != null ? t.getDescription().replace(",", ";") : ""
        )));
        
        return csv.toString();
    }
    
    public String generateReport(Long userId, String month) {
        User user = new User();
        user.setId(userId);
        List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user)
            .stream()
            .filter(t -> t.getDate().toString().startsWith(month))
            .collect(Collectors.toList());
        
        double income = transactions.stream()
            .filter(t -> "INCOME".equals(t.getType().name()))
            .mapToDouble(Transaction::getAmount)
            .sum();
        
        double expense = transactions.stream()
            .filter(t -> "EXPENSE".equals(t.getType().name()))
            .mapToDouble(Transaction::getAmount)
            .sum();
        
        return String.format("Monthly Report for %s\nIncome: %.2f\nExpense: %.2f\nBalance: %.2f",
            month, income, expense, income - expense);
    }
}
