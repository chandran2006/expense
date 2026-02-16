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
        List<String> recommendations = generateRecommendations(score, savingsRate, budgetDiscipline, expenseStability, user);
        
        // Calculate additional metrics
        double expenseRatio = (1 - savingsRate) * 100;
        double budgetAdherence = budgetDiscipline * 100;
        
        HealthScoreDTO dto = new HealthScoreDTO();
        dto.setScore(score);
        dto.setStatus(status);
        dto.setSavingsRate(savingsRate * 100);
        dto.setBudgetDiscipline(budgetDiscipline * 100);
        dto.setExpenseStability(expenseStability * 100);
        dto.setRecommendations(recommendations);
        dto.setExpenseRatio(expenseRatio);
        dto.setBudgetAdherence(budgetAdherence);
        
        return dto;
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
    
    private List<String> generateRecommendations(int score, double savingsRate, double budgetDiscipline, 
                                                   double expenseStability, User user) {
        List<String> recommendations = new ArrayList<>();
        
        // Savings rate recommendations
        if (savingsRate < 0.2) {
            recommendations.add("💰 Your savings rate is low. Try to save at least 20% of your income.");
            recommendations.add("📊 Review your expenses and identify areas where you can cut back.");
        } else if (savingsRate < 0.3) {
            recommendations.add("✅ Good savings rate! Aim for 30% to build wealth faster.");
        } else {
            recommendations.add("🌟 Excellent savings rate! You're on track for financial success.");
        }
        
        // Budget discipline recommendations
        if (budgetDiscipline < 0.5) {
            recommendations.add("⚠️ You're significantly exceeding your budget. Set realistic limits and track daily.");
            recommendations.add("🎯 Use the 50-30-20 rule: 50% needs, 30% wants, 20% savings.");
        } else if (budgetDiscipline < 0.8) {
            recommendations.add("📈 You're close to your budget limits. Monitor your spending more closely.");
        } else {
            recommendations.add("🎉 Great budget discipline! Keep up the good work.");
        }
        
        // Expense stability recommendations
        if (expenseStability < 0.5) {
            recommendations.add("📉 Your expenses vary significantly. Create a consistent spending plan.");
            recommendations.add("💡 Identify irregular expenses and plan for them in advance.");
        } else if (expenseStability < 0.8) {
            recommendations.add("📊 Your spending is moderately stable. Work on maintaining consistency.");
        } else {
            recommendations.add("✨ Your spending is very stable. This helps with financial planning.");
        }
        
        // Overall score recommendations
        if (score < 40) {
            recommendations.add("🚨 Your financial health needs immediate attention. Start by tracking all expenses.");
            recommendations.add("📱 Set up spending alerts and review your finances weekly.");
        } else if (score < 60) {
            recommendations.add("💪 You're making progress! Focus on building an emergency fund.");
        } else if (score < 80) {
            recommendations.add("🎯 You're doing well! Consider investing surplus funds for long-term growth.");
        } else {
            recommendations.add("🏆 Outstanding financial health! Consider advanced investment strategies.");
        }
        
        // Category-specific recommendations
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        
        Double totalExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                user, TransactionType.EXPENSE, startDate, endDate);
        
        if (totalExpense != null && totalExpense > 0) {
            recommendations.add("📝 Review your top spending categories and look for optimization opportunities.");
        }
        
        return recommendations;
    }
}
