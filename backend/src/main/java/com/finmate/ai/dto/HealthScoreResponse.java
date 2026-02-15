package com.finmate.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthScoreResponse {
    private Double score;
    private String status;
    private Double savingsRate;
    private Double budgetDiscipline;
    private Double expenseStability;
}
