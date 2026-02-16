package com.finmate.ai.controller;

import com.finmate.ai.dto.*;
import com.finmate.ai.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/features")
@RequiredArgsConstructor
@Tag(name = "Advanced Features", description = "Notifications, Receipts, Health Score, Alerts, Predictions")
public class FeaturesController {
    
    private final NotificationService notificationService;
    private final ReceiptService receiptService;
    private final HealthScoreService healthScoreService;
    private final DailySpendingAlertService dailySpendingAlertService;
    private final PredictionService predictionService;
    private final UserService userService;
    
    // Notifications
    @GetMapping("/notifications")
    @Operation(summary = "Get all notifications")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotifications() {
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", notificationService.getAllNotifications()));
    }
    
    @GetMapping("/notifications/unread")
    @Operation(summary = "Get unread notifications")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotifications() {
        return ResponseEntity.ok(ApiResponse.success("Unread notifications", notificationService.getUnreadNotifications()));
    }
    
    @GetMapping("/notifications/count")
    @Operation(summary = "Get unread count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        return ResponseEntity.ok(ApiResponse.success("Unread count", notificationService.getUnreadCount()));
    }
    
    @PutMapping("/notifications/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Marked as read", null));
    }
    
    @PutMapping("/notifications/read-all")
    @Operation(summary = "Mark all as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.success("All marked as read", null));
    }
    
    // Receipt Scanner
    @PostMapping("/receipt/upload")
    @Operation(summary = "Upload and scan receipt")
    public ResponseEntity<ApiResponse<ReceiptDTO>> uploadReceipt(@RequestParam("file") MultipartFile file) {
        ReceiptDTO receipt = receiptService.uploadAndProcessReceipt(file);
        return ResponseEntity.ok(ApiResponse.success("Receipt processed", receipt));
    }
    
    @GetMapping("/receipts")
    @Operation(summary = "Get all receipts")
    public ResponseEntity<ApiResponse<List<ReceiptDTO>>> getReceipts() {
        return ResponseEntity.ok(ApiResponse.success("Receipts retrieved", receiptService.getAllReceipts()));
    }
    
    // Health Score
    @GetMapping("/health-score")
    @Operation(summary = "Get financial health score")
    public ResponseEntity<ApiResponse<HealthScoreDTO>> getHealthScore() {
        HealthScoreDTO score = healthScoreService.calculateHealthScore();
        return ResponseEntity.ok(ApiResponse.success("Health score calculated", score));
    }
    
    // Daily Spending Alert
    @PostMapping("/alerts/check-daily-limit")
    @Operation(summary = "Check daily spending limit")
    public ResponseEntity<ApiResponse<Void>> checkDailyLimit() {
        dailySpendingAlertService.checkUserDailyLimit(userService.getCurrentUser());
        return ResponseEntity.ok(ApiResponse.success("Daily limit checked", null));
    }
    
    // Smart Predictions
    @GetMapping("/predictions/next-month")
    @Operation(summary = "Predict next month expenses")
    public ResponseEntity<ApiResponse<PredictionDTO>> predictNextMonth() {
        PredictionDTO prediction = predictionService.predictNextMonthExpense();
        return ResponseEntity.ok(ApiResponse.success("Prediction generated", prediction));
    }
    
    @GetMapping("/predictions/category/{category}")
    @Operation(summary = "Predict category spending")
    public ResponseEntity<ApiResponse<PredictionDTO>> predictCategory(@PathVariable String category) {
        PredictionDTO prediction = predictionService.predictNextMonthExpense();
        return ResponseEntity.ok(ApiResponse.success("Category prediction", prediction));
    }
}
