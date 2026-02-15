package com.finmate.ai.controller;

import com.finmate.ai.dto.ApiResponse;
import com.finmate.ai.dto.HealthScoreDTO;
import com.finmate.ai.dto.PredictionDTO;
import com.finmate.ai.service.HealthScoreService;
import com.finmate.ai.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Analytics", description = "Financial analytics APIs")
public class AnalyticsController {
    
    private final HealthScoreService healthScoreService;
    private final PredictionService predictionService;
    
    @GetMapping("/health-score")
    @Operation(summary = "Get financial health score")
    public ResponseEntity<ApiResponse<HealthScoreDTO>> getHealthScore() {
        HealthScoreDTO healthScore = healthScoreService.calculateHealthScore();
        return ResponseEntity.ok(ApiResponse.success("Health score calculated successfully", healthScore));
    }
    
    @GetMapping("/predict-expense")
    @Operation(summary = "Predict next month expense")
    public ResponseEntity<ApiResponse<PredictionDTO>> predictExpense() {
        PredictionDTO prediction = predictionService.predictNextMonthExpense();
        return ResponseEntity.ok(ApiResponse.success("Expense prediction generated successfully", prediction));
    }
}
