package com.finmate.ai.service;

import com.finmate.ai.entity.NotificationType;
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
public class DailySpendingService {
    
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final NotificationService notificationService;
    
    public void checkDailyLimit(User user) {
        YearMonth currentMonth = YearMonth.now();
        String month = currentMonth.toString();
        
        budgetRepository.findByUserAndMonth(user, month).ifPresent(budget -> {
            int totalDaysInMonth = currentMonth.lengthOfMonth();
            double dailyLimit = budget.getLimitAmount() / totalDaysInMonth;
            
            LocalDate today = LocalDate.now();
            Double todayExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, today, today);
            
            todayExpense = todayExpense != null ? todayExpense : 0.0;
            
            if (todayExpense > dailyLimit) {
                String message = String.format(
                        "Daily spending limit exceeded! Today: ₹%.2f, Limit: ₹%.2f",
                        todayExpense, dailyLimit
                );
                notificationService.createNotification(user, message, NotificationType.ALERT);
            }
        });
    }
}
