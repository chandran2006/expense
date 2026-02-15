package com.finmate.ai.service;

import com.finmate.ai.dto.PredictionDTO;
import com.finmate.ai.entity.TransactionType;
import com.finmate.ai.entity.User;
import com.finmate.ai.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionService {
    
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    
    public PredictionDTO predictNextMonthExpense() {
        User user = userService.getCurrentUser();
        
        List<Double> last3MonthsExpenses = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            YearMonth month = YearMonth.now().minusMonths(i);
            LocalDate startDate = month.atDay(1);
            LocalDate endDate = month.atEndOfMonth();
            
            Double expense = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, startDate, endDate);
            last3MonthsExpenses.add(expense != null ? expense : 0.0);
        }
        
        if (last3MonthsExpenses.isEmpty() || last3MonthsExpenses.stream().allMatch(e -> e == 0)) {
            return new PredictionDTO(0.0, "Low");
        }
        
        // Calculate moving average
        double predictedExpense = last3MonthsExpenses.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        
        // Calculate confidence level based on variance
        double average = predictedExpense;
        double variance = last3MonthsExpenses.stream()
                .mapToDouble(e -> Math.pow(e - average, 2))
                .average()
                .orElse(0);
        
        double stdDev = Math.sqrt(variance);
        double coefficientOfVariation = average > 0 ? stdDev / average : 0;
        
        String confidenceLevel;
        if (coefficientOfVariation < 0.15) {
            confidenceLevel = "High";
        } else if (coefficientOfVariation < 0.30) {
            confidenceLevel = "Medium";
        } else {
            confidenceLevel = "Low";
        }
        
        return new PredictionDTO(predictedExpense, confidenceLevel);
    }
}
