package com.finmate.ai.service;

import com.finmate.ai.dto.HealthScoreDTO;
import com.finmate.ai.entity.TransactionType;
import com.finmate.ai.entity.User;
import com.finmate.ai.repository.BudgetRepository;
import com.finmate.ai.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthScoreService {
    
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final UserService userService;
    
    public HealthScoreDTO calculateHealthScore() {
        User user = userService.getCurrentUser();
        
        // Calculate Savings Rate (40%)
        double savingsRate = calculateSavingsRate(user);
        
        // Calculate Budget Discipline (30%)
        double budgetDiscipline = calculateBudgetDiscipline(user);
        
        // Calculate Expense Stability (30%)
        double expenseStability = calculateExpenseStability(user);
        
        // Calculate total score
        int score = (int) Math.round(
                (savingsRate * 40) + 
                (budgetDiscipline * 30) + 
                (expenseStability * 30)
        );
        
        String status = getStatus(score);
        
        return new HealthScoreDTO(score, status, savingsRate, budgetDiscipline, expenseStability);
    }
    
    private double calculateSavingsRate(User user) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        
        Double income = transactionRepository.sumByUserAndTypeAndDateBetween(
                user, TransactionType.INCOME, startDate, endDate);
        Double expense = transactionRepository.sumByUserAndTypeAndDateBetween(
                user, TransactionType.EXPENSE, startDate, endDate);
        
        income = income != null ? income : 0.0;
        expense = expense != null ? expense : 0.0;
        
        if (income == 0) return 0.0;
        
        return Math.max(0, Math.min(1, (income - expense) / income));
    }
    
    private double calculateBudgetDiscipline(User user) {
        YearMonth currentMonth = YearMonth.now();
        String month = currentMonth.toString();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        
        return budgetRepository.findByUserAndMonth(user, month)
                .map(budget -> {
                    Double expense = transactionRepository.sumByUserAndTypeAndDateBetween(
                            user, TransactionType.EXPENSE, startDate, endDate);
                    expense = expense != null ? expense : 0.0;
                    
                    if (expense <= budget.getLimitAmount()) {
                        return 1.0;
                    } else {
                        double overSpent = expense - budget.getLimitAmount();
                        return Math.max(0, 1 - (overSpent / budget.getLimitAmount()));
                    }
                })
                .orElse(0.5);
    }
    
    private double calculateExpenseStability(User user) {
        List<Double> last3MonthsExpenses = new ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            YearMonth month = YearMonth.now().minusMonths(i);
            LocalDate startDate = month.atDay(1);
            LocalDate endDate = month.atEndOfMonth();
            
            Double expense = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, startDate, endDate);
            last3MonthsExpenses.add(expense != null ? expense : 0.0);
        }
        
        if (last3MonthsExpenses.isEmpty()) return 0.5;
        
        double average = last3MonthsExpenses.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        
        if (average == 0) return 1.0;
        
        double variance = last3MonthsExpenses.stream()
                .mapToDouble(e -> Math.pow(e - average, 2))
                .average()
                .orElse(0);
        
        double stdDev = Math.sqrt(variance);
        double coefficientOfVariation = stdDev / average;
        
        return Math.max(0, Math.min(1, 1 - coefficientOfVariation));
    }
    
    private String getStatus(int score) {
        if (score >= 80) return "Excellent";
        if (score >= 60) return "Good";
        if (score >= 40) return "Fair";
        return "Poor";
    }
}
