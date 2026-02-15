package com.finmate.ai.controller;

import com.finmate.ai.dto.ApiResponse;
import com.finmate.ai.dto.BudgetDTO;
import com.finmate.ai.dto.BudgetRequest;
import com.finmate.ai.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Budget", description = "Budget management APIs")
public class BudgetController {
    
    private final BudgetService budgetService;
    
    @PostMapping
    @Operation(summary = "Set monthly budget")
    public ResponseEntity<ApiResponse<BudgetDTO>> setBudget(@Valid @RequestBody BudgetRequest request) {
        BudgetDTO budget = budgetService.setBudget(request);
        String message = budget.isExceeded() 
                ? "Budget set successfully. Warning: You have exceeded your budget!" 
                : "Budget set successfully";
        return ResponseEntity.ok(ApiResponse.success(message, budget));
    }
    
    @GetMapping("/{month}")
    @Operation(summary = "Get budget status", description = "Month format: YYYY-MM (e.g., 2024-01)")
    public ResponseEntity<ApiResponse<BudgetDTO>> getBudgetStatus(@PathVariable String month) {
        BudgetDTO budget = budgetService.getBudgetStatus(month);
        String message = budget.isExceeded() 
                ? "Warning: Budget exceeded!" 
                : "Budget status retrieved successfully";
        return ResponseEntity.ok(ApiResponse.success(message, budget));
    }
}
