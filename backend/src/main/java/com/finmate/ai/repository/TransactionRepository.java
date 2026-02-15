package com.finmate.ai.repository;

import com.finmate.ai.entity.Transaction;
import com.finmate.ai.entity.TransactionType;
import com.finmate.ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByDateDesc(User user);
    
    List<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.type = :type AND t.date BETWEEN :startDate AND :endDate")
    Double sumByUserAndTypeAndDateBetween(@Param("user") User user, @Param("type") TransactionType type, 
                                          @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.type = :type AND t.date BETWEEN :startDate AND :endDate GROUP BY t.category")
    List<Object[]> sumByCategoryAndUserAndTypeAndDateBetween(@Param("user") User user, @Param("type") TransactionType type,
                                                              @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
