package com.finmate.ai.service;

import com.finmate.ai.dto.ChatResponse;
import com.finmate.ai.dto.HealthScoreDTO;
import com.finmate.ai.entity.ChatHistory;
import com.finmate.ai.entity.TransactionType;
import com.finmate.ai.entity.User;
import com.finmate.ai.repository.ChatHistoryRepository;
import com.finmate.ai.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
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
    private final HealthScoreService healthScoreService;
    
    public ChatResponse processMessage(String message) {
        User user = null;
        try {
            user = userService.getCurrentUser();
        } catch (Exception e) {
            // User not authenticated, use demo mode
        }
        
        String response = generateResponse(message, user);
        
        if (user != null) {
            saveChatHistoryAsync(user, message, response);
        }
        
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
        
        // If no user, provide general advice
        if (user == null) {
            return handleGuestQuery(lowerMessage);
        }
        
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        
        // Health score query
        if (lowerMessage.contains("health score") || lowerMessage.contains("financial health") || 
            lowerMessage.contains("how am i doing")) {
            HealthScoreDTO healthScore = healthScoreService.calculateHealthScore();
            return String.format("🎯 Your Financial Health Score: %d/100 (%s)\n\n" +
                    "💰 Savings Rate: %.1f%%\n" +
                    "🎯 Budget Adherence: %.1f%%\n" +
                    "📊 Expense Stability: %.1f%%\n\n" +
                    "Keep tracking your finances to improve your score!",
                    healthScore.getScore(), healthScore.getStatus(),
                    healthScore.getSavingsRate(), healthScore.getBudgetAdherence(),
                    healthScore.getExpenseStability());
        }
        
        // Total expense query
        if (lowerMessage.contains("total expense") || lowerMessage.contains("how much did i spend") ||
            lowerMessage.contains("total spending")) {
            Double totalExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, startDate, endDate);
            totalExpense = totalExpense != null ? totalExpense : 0.0;
            
            Double lastMonthExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, 
                    currentMonth.minusMonths(1).atDay(1), 
                    currentMonth.minusMonths(1).atEndOfMonth());
            lastMonthExpense = lastMonthExpense != null ? lastMonthExpense : 0.0;
            
            String trend = "";
            if (lastMonthExpense > 0) {
                double change = ((totalExpense - lastMonthExpense) / lastMonthExpense) * 100;
                if (change > 0) {
                    trend = String.format(" (↑ %.1f%% from last month)", change);
                } else if (change < 0) {
                    trend = String.format(" (↓ %.1f%% from last month)", Math.abs(change));
                }
            }
            
            return String.format("💸 Total Spending This Month: ₹%.2f%s\n\n" +
                    "Last Month: ₹%.2f\n" +
                    "Tip: Track daily to stay within budget!", 
                    totalExpense, trend, lastMonthExpense);
        }
        
        // Total income query
        if (lowerMessage.contains("total income") || lowerMessage.contains("how much did i earn") ||
            lowerMessage.contains("earnings")) {
            Double totalIncome = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.INCOME, startDate, endDate);
            totalIncome = totalIncome != null ? totalIncome : 0.0;
            return String.format("💵 Total Income This Month: ₹%.2f\n\n" +
                    "Keep diversifying your income sources!", totalIncome);
        }
        
        // Category-specific queries
        String[] categories = {"food", "transport", "shopping", "entertainment", "bills", 
                               "health", "healthcare", "education", "salary", "freelance", "investment"};
        for (String category : categories) {
            if (lowerMessage.contains(category)) {
                Map<String, Double> categoryExpenses = transactionService.getCategoryWiseExpense(user, startDate, endDate);
                Double amount = categoryExpenses.getOrDefault(capitalize(category), 0.0);
                
                // Calculate percentage of total
                Double totalExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                        user, TransactionType.EXPENSE, startDate, endDate);
                totalExpense = totalExpense != null ? totalExpense : 0.0;
                double percentage = totalExpense > 0 ? (amount / totalExpense) * 100 : 0;
                
                return String.format("📊 %s Spending: ₹%.2f (%.1f%% of total expenses)\n\n" +
                        "Tip: Compare this with your budget allocation!", 
                        capitalize(category), amount, percentage);
            }
        }
        
        // Budget query
        if (lowerMessage.contains("budget") || lowerMessage.contains("limit") || 
            lowerMessage.contains("exceed") || lowerMessage.contains("overspending")) {
            Double totalExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, startDate, endDate);
            totalExpense = totalExpense != null ? totalExpense : 0.0;
            
            return String.format("🎯 Current Spending: ₹%.2f\n\n" +
                    "Check your Budget page to see if you're within limits.\n" +
                    "Set alerts to get notified when you reach 75%%, 90%%, and 100%% of your budget!", 
                    totalExpense);
        }
        
        // Saving tips
        if (lowerMessage.contains("saving") || lowerMessage.contains("save money") || 
            lowerMessage.contains("tips") || lowerMessage.contains("advice")) {
            return "💡 Smart Saving Tips:\n\n" +
                    "1. 📊 Track Every Expense - Awareness is the first step\n" +
                    "2. 🎯 Set Realistic Budgets - Use the 50-30-20 rule\n" +
                    "3. ✂️ Cut Subscriptions - Cancel unused services\n" +
                    "4. 🍳 Cook at Home - Save 30-40% on food costs\n" +
                    "5. 💰 Automate Savings - Save before you spend\n" +
                    "6. 🛒 Wait 24hrs - Avoid impulse purchases\n" +
                    "7. 💳 Use Cash - Physical money makes spending real\n" +
                    "8. 🎯 Emergency Fund - Build 3-6 months expenses\n\n" +
                    "Start with one tip and build from there!";
        }
        
        // Balance query
        if (lowerMessage.contains("balance") || lowerMessage.contains("left") || 
            lowerMessage.contains("remaining") || lowerMessage.contains("net")) {
            Double totalIncome = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.INCOME, startDate, endDate);
            Double totalExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, startDate, endDate);
            totalIncome = totalIncome != null ? totalIncome : 0.0;
            totalExpense = totalExpense != null ? totalExpense : 0.0;
            double balance = totalIncome - totalExpense;
            double savingsRate = totalIncome > 0 ? (balance / totalIncome) * 100 : 0;
            
            String emoji = balance > 0 ? "😊" : "😟";
            return String.format("%s Current Balance: ₹%.2f\n\n" +
                    "💵 Income: ₹%.2f\n" +
                    "💸 Expenses: ₹%.2f\n" +
                    "📊 Savings Rate: %.1f%%\n\n" +
                    "Aim for at least 20%% savings rate!", 
                    emoji, balance, totalIncome, totalExpense, savingsRate);
        }
        
        // Comparison queries
        if (lowerMessage.contains("compare") || lowerMessage.contains("last month") || 
            lowerMessage.contains("previous month") || lowerMessage.contains("trend")) {
            Double thisMonth = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, startDate, endDate);
            Double lastMonth = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE,
                    currentMonth.minusMonths(1).atDay(1),
                    currentMonth.minusMonths(1).atEndOfMonth());
            
            thisMonth = thisMonth != null ? thisMonth : 0.0;
            lastMonth = lastMonth != null ? lastMonth : 0.0;
            
            double change = lastMonth > 0 ? ((thisMonth - lastMonth) / lastMonth) * 100 : 0;
            String trend = change > 0 ? "↑ increased" : "↓ decreased";
            String emoji = change > 0 ? "📈" : "📉";
            
            return String.format("%s Spending Comparison:\n\n" +
                    "This Month: ₹%.2f\n" +
                    "Last Month: ₹%.2f\n" +
                    "Change: %s by %.1f%%\n\n" +
                    "%s",
                    emoji, thisMonth, lastMonth, trend, Math.abs(change),
                    change > 0 ? "Try to identify what caused the increase." : "Great job reducing expenses!");
        }
        
        // Top spending query
        if (lowerMessage.contains("top spending") || lowerMessage.contains("most spent") || 
            lowerMessage.contains("biggest expense") || lowerMessage.contains("where did i spend")) {
            Map<String, Double> categoryExpenses = transactionService.getCategoryWiseExpense(user, startDate, endDate);
            
            if (categoryExpenses.isEmpty()) {
                return "📊 No expenses recorded yet this month. Start tracking to see insights!";
            }
            
            StringBuilder response = new StringBuilder("📊 Top Spending Categories:\n\n");
            categoryExpenses.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(5)
                    .forEach(entry -> response.append(String.format("• %s: ₹%.2f\n", 
                            entry.getKey(), entry.getValue())));
            
            response.append("\nFocus on your top categories to maximize savings!");
            return response.toString();
        }
        
        // Help/capabilities query
        if (lowerMessage.contains("help") || lowerMessage.contains("what can you do") || 
            lowerMessage.contains("capabilities") || lowerMessage.contains("commands")) {
            return "🤖 I'm your AI Financial Assistant! I can help with:\n\n" +
                    "📊 Financial Analysis:\n" +
                    "  • 'What's my health score?'\n" +
                    "  • 'How much did I spend?'\n" +
                    "  • 'What's my balance?'\n" +
                    "  • 'Compare with last month'\n\n" +
                    "🎯 Category Insights:\n" +
                    "  • 'How much on food?'\n" +
                    "  • 'Top spending categories'\n" +
                    "  • 'Shopping expenses'\n\n" +
                    "💡 Smart Advice:\n" +
                    "  • 'Give me saving tips'\n" +
                    "  • 'Budget advice'\n" +
                    "  • 'How to save money?'\n\n" +
                    "Just ask naturally - I understand context!";
        }
        
        // Default response with context
        return "👋 I'm here to help with your finances!\n\n" +
                "Try asking:\n" +
                "• 'What's my financial health score?'\n" +
                "• 'How much did I spend this month?'\n" +
                "• 'Show my top spending categories'\n" +
                "• 'Give me saving tips'\n" +
                "• 'Compare with last month'\n" +
                "• 'What's my balance?'\n\n" +
                "Type 'help' to see all my capabilities!";
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    private String handleGuestQuery(String lowerMessage) {
        // Saving tips
        if (lowerMessage.contains("saving") || lowerMessage.contains("save money") || 
            lowerMessage.contains("tips") || lowerMessage.contains("advice")) {
            return "💡 Smart Saving Tips:\n\n" +
                    "1. 📊 Track Every Expense - Awareness is the first step\n" +
                    "2. 🎯 Set Realistic Budgets - Use the 50-30-20 rule\n" +
                    "3. ✂️ Cut Subscriptions - Cancel unused services\n" +
                    "4. 🍳 Cook at Home - Save 30-40% on food costs\n" +
                    "5. 💰 Automate Savings - Save before you spend\n" +
                    "6. 🛒 Wait 24hrs - Avoid impulse purchases\n" +
                    "7. 💳 Use Cash - Physical money makes spending real\n" +
                    "8. 🎯 Emergency Fund - Build 3-6 months expenses\n\n" +
                    "Login to get personalized insights based on your data!";
        }
        
        // Help query
        if (lowerMessage.contains("help") || lowerMessage.contains("what can you do")) {
            return "🤖 I'm your AI Financial Assistant!\n\n" +
                    "To get personalized insights, please login first.\n\n" +
                    "Once logged in, I can help with:\n" +
                    "📊 Financial health analysis\n" +
                    "💰 Spending insights\n" +
                    "🎯 Budget tracking\n" +
                    "💡 Personalized saving tips\n" +
                    "📈 Expense comparisons\n\n" +
                    "For now, try asking: 'Give me saving tips'";
        }
        
        // Default guest response
        return "👋 Welcome to FinMate AI!\n\n" +
                "To get personalized financial insights, please login or signup.\n\n" +
                "For general advice, try asking:\n" +
                "• 'Give me saving tips'\n" +
                "• 'How to save money?'\n" +
                "• 'Help'\n\n" +
                "Login to unlock:\n" +
                "✨ Your financial health score\n" +
                "✨ Spending analysis\n" +
                "✨ Budget tracking\n" +
                "✨ Personalized recommendations";
    }
}
