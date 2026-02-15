package com.finmate.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BudgetRequest {
    @NotBlank(message = "Month is required")
    private String month;
    
    @NotNull(message = "Limit amount is required")
    @Positive(message = "Limit amount must be positive")
    private Double limitAmount;
}
