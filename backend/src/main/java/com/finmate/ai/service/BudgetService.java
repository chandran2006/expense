package com.finmate.ai.service;

import com.finmate.ai.dto.BudgetDTO;
import com.finmate.ai.dto.BudgetRequest;
import com.finmate.ai.entity.Budget;
import com.finmate.ai.entity.TransactionType;
import com.finmate.ai.entity.User;
import com.finmate.ai.repository.BudgetRepository;
import com.finmate.ai.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    
    public BudgetDTO setBudget(BudgetRequest request) {
        User user = userService.getCurrentUser();
        
        Budget budget = budgetRepository.findByUserAndMonth(user, request.getMonth())
                .orElse(new Budget());
        
        budget.setUser(user);
        budget.setMonth(request.getMonth());
        budget.setLimitAmount(request.getLimitAmount());
        
        budgetRepository.save(budget);
        return getBudgetStatus(request.getMonth());
    }
    
    public BudgetDTO getBudgetStatus(String month) {
        User user = userService.getCurrentUser();
        Budget budget = budgetRepository.findByUserAndMonth(user, month)
                .orElse(null);
        
        if (budget == null) {
            return new BudgetDTO(null, month, 0.0, 0.0, false);
        }
        
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        Double currentSpending = transactionRepository.sumByUserAndTypeAndDateBetween(
                user, TransactionType.EXPENSE, startDate, endDate);
        currentSpending = currentSpending != null ? currentSpending : 0.0;
        
        boolean exceeded = currentSpending > budget.getLimitAmount();
        
        return new BudgetDTO(
                budget.getId(),
                budget.getMonth(),
                budget.getLimitAmount(),
                currentSpending,
                exceeded
        );
    }
    
    public void checkMonthlyBudget(User user) {
        String currentMonth = YearMonth.now().toString();
        Budget budget = budgetRepository.findByUserAndMonth(user, currentMonth)
                .orElse(null);
        
        if (budget == null) return;
        
        YearMonth yearMonth = YearMonth.now();
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        Double currentSpending = transactionRepository.sumByUserAndTypeAndDateBetween(
                user, TransactionType.EXPENSE, startDate, endDate);
        currentSpending = currentSpending != null ? currentSpending : 0.0;
        
        double percentage = (currentSpending / budget.getLimitAmount()) * 100;
        
        if (currentSpending > budget.getLimitAmount()) {
            notificationService.createNotification(
                    user,
                    String.format("🚨 Budget exceeded! Spent: ₹%.2f, Limit: ₹%.2f (%.0f%%)", 
                        currentSpending, budget.getLimitAmount(), percentage),
                    com.finmate.ai.entity.NotificationType.WARNING
            );
        } else if (percentage >= 90) {
            notificationService.createNotification(
                    user,
                    String.format("⚠️ Budget alert! You've used %.0f%% of your monthly budget (₹%.2f/₹%.2f)", 
                        percentage, currentSpending, budget.getLimitAmount()),
                    com.finmate.ai.entity.NotificationType.ALERT
            );
        } else if (percentage >= 75) {
            notificationService.createNotification(
                    user,
                    String.format("💡 Budget reminder: %.0f%% used (₹%.2f/₹%.2f)", 
                        percentage, currentSpending, budget.getLimitAmount()),
                    com.finmate.ai.entity.NotificationType.INFO
            );
        }
    }
}
