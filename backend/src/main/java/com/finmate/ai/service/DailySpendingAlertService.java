package com.finmate.ai.service;

import com.finmate.ai.entity.NotificationType;
import com.finmate.ai.entity.TransactionType;
import com.finmate.ai.entity.User;
import com.finmate.ai.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DailySpendingAlertService {
    
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    
    private static final double DAILY_LIMIT = 1000.0; // Default daily limit
    
    @Scheduled(cron = "0 0 20 * * *") // Run at 8 PM daily
    public void checkDailySpendingLimit() {
        // Check for all users (in production, iterate through active users)
        try {
            User user = userService.getCurrentUser();
            checkUserDailyLimit(user);
        } catch (Exception e) {
            // Handle when no user context
        }
    }
    
    public void checkUserDailyLimit(User user) {
        LocalDate today = LocalDate.now();
        Double dailySpending = transactionRepository.sumByUserAndTypeAndDateBetween(
            user, TransactionType.EXPENSE, today, today
        );
        
        if (dailySpending != null && dailySpending > DAILY_LIMIT) {
            String message = String.format(
                "⚠️ Daily spending limit exceeded! You spent ₹%.2f today (Limit: ₹%.2f)",
                dailySpending, DAILY_LIMIT
            );
            notificationService.createNotification(user, message, NotificationType.ALERT);
        } else if (dailySpending != null && dailySpending > DAILY_LIMIT * 0.8) {
            String message = String.format(
                "⚡ You're approaching your daily limit! Spent ₹%.2f of ₹%.2f",
                dailySpending, DAILY_LIMIT
            );
            notificationService.createNotification(user, message, NotificationType.WARNING);
        }
    }
}
