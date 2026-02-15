package com.finmate.ai.dto;

import com.finmate.ai.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private TransactionType type;
    private String category;
    private Double amount;
    private String description;
    private LocalDate date;
}
