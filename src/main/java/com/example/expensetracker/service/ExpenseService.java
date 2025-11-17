package com.example.expensetracker.service;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    @Transactional
    public Expense saveExpense(Expense expense, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getExpensesByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return expenseRepository.findByUser(user);
    }

    public List<Expense> searchExpenses(String username, String category, LocalDate startDate, 
                                       LocalDate endDate, BigDecimal minAmount, String name) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Expense> expenses = expenseRepository.findByUser(user);
        
        if (category != null && !category.isEmpty()) {
            expenses = expenses.stream()
                    .filter(e -> e.getCategory().toLowerCase().contains(category.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (startDate != null && endDate != null) {
            expenses = expenses.stream()
                    .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }
        
        if (minAmount != null) {
            expenses = expenses.stream()
                    .filter(e -> e.getAmount().compareTo(minAmount) >= 0)
                    .collect(Collectors.toList());
        }
        
        if (name != null && !name.isEmpty()) {
            expenses = expenses.stream()
                    .filter(e -> e.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return expenses;
    }

    public Map<String, BigDecimal> getExpenseSumByCategory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Object[]> results = expenseRepository.findExpenseSumByCategory(user);
        Map<String, BigDecimal> categorySum = new HashMap<>();
        
        for (Object[] result : results) {
            String category = (String) result[0];
            BigDecimal sum = (BigDecimal) result[1];
            categorySum.put(category, sum);
        }
        
        return categorySum;
    }

    public Map<String, Map<String, BigDecimal>> getExpenseSumByCategoryAndSubCategory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Object[]> results = expenseRepository.findExpenseSumByCategoryAndSubCategory(user);
        Map<String, Map<String, BigDecimal>> categorizedExpenses = new HashMap<>();
        
        for (Object[] result : results) {
            String category = (String) result[0];
            String subCategory = (String) result[1];
            BigDecimal sum = (BigDecimal) result[2];
            
            categorizedExpenses.computeIfAbsent(category, k -> new HashMap<>())
                    .put(subCategory != null ? subCategory : "Uncategorized", sum);
        }
        
        return categorizedExpenses;
    }

    public Map<Integer, BigDecimal> getMonthlyExpenseSumByYear(String username, int year) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Object[]> results = expenseRepository.findMonthlyExpenseSumByYear(user, year);
        Map<Integer, BigDecimal> monthlySum = new HashMap<>();
        
        for (Object[] result : results) {
            Integer month = ((Number) result[0]).intValue();
            BigDecimal sum = (BigDecimal) result[1];
            monthlySum.put(month, sum);
        }
        
        return monthlySum;
    }

    public Map<String, BigDecimal> getExpenseSumByCard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Object[]> results = expenseRepository.findExpenseSumByCard(user);
        Map<String, BigDecimal> cardSum = new HashMap<>();
        
        for (Object[] result : results) {
            String card = (String) result[0];
            BigDecimal sum = (BigDecimal) result[1];
            cardSum.put(card != null ? card : "Cash/Unknown", sum);
        }
        
        return cardSum;
    }
}
