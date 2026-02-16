package com.finmate.ai.service;

import com.finmate.ai.dto.ChatResponse;
import com.finmate.ai.entity.ChatHistory;
import com.finmate.ai.entity.TransactionType;
import com.finmate.ai.entity.User;
import com.finmate.ai.repository.ChatHistoryRepository;
import com.finmate.ai.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AiInsightService {
    
    private final TransactionRepository transactionRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final UserService userService;
    private final TransactionService transactionService;
    
    public ChatResponse processMessage(String message) {
        User user = userService.getCurrentUser();
        String response = generateResponse(message, user);
        
        saveChatHistoryAsync(user, message, response);
        
        return new ChatResponse(response);
    }
    
    @Async
    public CompletableFuture<Void> saveChatHistoryAsync(User user, String message, String response) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setUser(user);
        chatHistory.setMessage(message);
        chatHistory.setResponse(response);
        chatHistory.setTimestamp(LocalDateTime.now());
        chatHistoryRepository.save(chatHistory);
        return CompletableFuture.completedFuture(null);
    }
    
    private String generateResponse(String message, User user) {
        String lowerMessage = message.toLowerCase();
        
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        
        // Total expense query
        if (lowerMessage.contains("total expense") || lowerMessage.contains("how much did i spend")) {
            Double totalExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, startDate, endDate);
            totalExpense = totalExpense != null ? totalExpense : 0.0;
            return String.format("You spent ₹%.2f in total this month.", totalExpense);
        }
        
        // Total income query
        if (lowerMessage.contains("total income") || lowerMessage.contains("how much did i earn")) {
            Double totalIncome = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.INCOME, startDate, endDate);
            totalIncome = totalIncome != null ? totalIncome : 0.0;
            return String.format("You earned ₹%.2f in total this month.", totalIncome);
        }
        
        // Category-specific queries
        String[] categories = {"food", "transport", "shopping", "entertainment", "bills", "health", "education"};
        for (String category : categories) {
            if (lowerMessage.contains(category)) {
                Map<String, Double> categoryExpenses = transactionService.getCategoryWiseExpense(user, startDate, endDate);
                Double amount = categoryExpenses.getOrDefault(capitalize(category), 0.0);
                return String.format("You spent ₹%.2f on %s this month.", amount, capitalize(category));
            }
        }
        
        // Overspending query
        if (lowerMessage.contains("overspending") || lowerMessage.contains("budget") || lowerMessage.contains("exceed")) {
            Double totalExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, startDate, endDate);
            totalExpense = totalExpense != null ? totalExpense : 0.0;
            return String.format("Your total spending this month is ₹%.2f. Check your budget settings to see if you're within limits.", totalExpense);
        }
        
        // Saving tips
        if (lowerMessage.contains("saving") || lowerMessage.contains("save money") || lowerMessage.contains("tips")) {
            return "Here are some saving tips:\n" +
                    "1. Track all your expenses daily\n" +
                    "2. Set a monthly budget and stick to it\n" +
                    "3. Reduce unnecessary subscriptions\n" +
                    "4. Cook at home instead of eating out\n" +
                    "5. Use the 50-30-20 rule: 50% needs, 30% wants, 20% savings";
        }
        
        // Balance query
        if (lowerMessage.contains("balance") || lowerMessage.contains("left")) {
            Double totalIncome = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.INCOME, startDate, endDate);
            Double totalExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, startDate, endDate);
            totalIncome = totalIncome != null ? totalIncome : 0.0;
            totalExpense = totalExpense != null ? totalExpense : 0.0;
            double balance = totalIncome - totalExpense;
            return String.format("Your current balance this month is ₹%.2f (Income: ₹%.2f - Expense: ₹%.2f)", 
                    balance, totalIncome, totalExpense);
        }
        
        // Default response
        return "I can help you with:\n" +
                "- Total expenses and income\n" +
                "- Category-wise spending (food, transport, shopping, etc.)\n" +
                "- Budget tracking and overspending alerts\n" +
                "- Saving tips and financial advice\n" +
                "- Current balance\n\n" +
                "Try asking: 'How much did I spend on food?' or 'Give me saving tips'";
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
