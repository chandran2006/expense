package com.finmate.ai.dto;

import com.finmate.ai.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionRequest {
    @NotNull(message = "Transaction type is required")
    private TransactionType type;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    private String description;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
}
