package com.example.expensetracker.repository;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findByUser(User user);
    
    List<Expense> findByUserAndCategoryContainingIgnoreCase(User user, String category);
    
    List<Expense> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    List<Expense> findByUserAndAmountGreaterThanEqual(User user, BigDecimal amount);
    
    List<Expense> findByUserAndNameContainingIgnoreCase(User user, String name);
    
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user = ?1 GROUP BY e.category")
    List<Object[]> findExpenseSumByCategory(User user);
    
    @Query("SELECT e.category, e.subCategory, SUM(e.amount) FROM Expense e WHERE e.user = ?1 GROUP BY e.category, e.subCategory")
    List<Object[]> findExpenseSumByCategoryAndSubCategory(User user);
    
    @Query("SELECT FUNCTION('MONTH', e.date) as month, SUM(e.amount) FROM Expense e WHERE e.user = ?1 AND FUNCTION('YEAR', e.date) = ?2 GROUP BY FUNCTION('MONTH', e.date)")
    List<Object[]> findMonthlyExpenseSumByYear(User user, int year);
    
    @Query("SELECT e.cardUsed, SUM(e.amount) FROM Expense e WHERE e.user = ?1 GROUP BY e.cardUsed")
    List<Object[]> findExpenseSumByCard(User user);
}
