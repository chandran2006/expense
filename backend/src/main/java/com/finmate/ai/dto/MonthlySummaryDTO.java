package com.finmate.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummaryDTO {
    private Double totalIncome;
    private Double totalExpense;
    private Double balance;
    private Map<String, Double> categoryWiseExpense;
}
