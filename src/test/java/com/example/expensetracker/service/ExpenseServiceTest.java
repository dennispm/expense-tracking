package com.example.expensetracker.service;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private User testUser;
    private Expense testExpense;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setName("Groceries");
        testExpense.setAmount(new BigDecimal("100.00"));
        testExpense.setDate(LocalDate.now());
        testExpense.setCategory("Food");
        testExpense.setUser(testUser);
    }

    @Test
    void testSaveExpense() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        Expense result = expenseService.saveExpense(testExpense, "testuser");

        assertNotNull(result);
        verify(userRepository).findByUsername("testuser");
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void testSaveExpenseUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            expenseService.saveExpense(testExpense, "nonexistent");
        });

        verify(userRepository).findByUsername("nonexistent");
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void testGetAllExpenses() {
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseRepository.findAll()).thenReturn(expenses);

        List<Expense> result = expenseService.getAllExpenses();

        assertEquals(1, result.size());
        verify(expenseRepository).findAll();
    }

    @Test
    void testGetExpensesByUser() {
        List<Expense> expenses = Arrays.asList(testExpense);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUser(testUser)).thenReturn(expenses);

        List<Expense> result = expenseService.getExpensesByUser("testuser");

        assertEquals(1, result.size());
        verify(userRepository).findByUsername("testuser");
        verify(expenseRepository).findByUser(testUser);
    }

    @Test
    void testGetExpensesByUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            expenseService.getExpensesByUser("nonexistent");
        });

        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void testSearchExpensesWithCategory() {
        List<Expense> expenses = Arrays.asList(testExpense);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUser(testUser)).thenReturn(expenses);

        List<Expense> result = expenseService.searchExpenses("testuser", "Food", null, null, null, null);

        assertEquals(1, result.size());
        verify(expenseRepository).findByUser(testUser);
    }

    @Test
    void testSearchExpensesWithDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<Expense> expenses = Arrays.asList(testExpense);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUser(testUser)).thenReturn(expenses);

        List<Expense> result = expenseService.searchExpenses("testuser", null, startDate, endDate, null, null);

        assertEquals(1, result.size());
        verify(expenseRepository).findByUser(testUser);
    }

    @Test
    void testSearchExpensesWithMinAmount() {
        List<Expense> expenses = Arrays.asList(testExpense);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUser(testUser)).thenReturn(expenses);

        List<Expense> result = expenseService.searchExpenses("testuser", null, null, null, new BigDecimal("50.00"), null);

        assertEquals(1, result.size());
        verify(expenseRepository).findByUser(testUser);
    }

    @Test
    void testSearchExpensesWithName() {
        List<Expense> expenses = Arrays.asList(testExpense);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUser(testUser)).thenReturn(expenses);

        List<Expense> result = expenseService.searchExpenses("testuser", null, null, null, null, "Groceries");

        assertEquals(1, result.size());
        verify(expenseRepository).findByUser(testUser);
    }

    @Test
    void testSearchExpensesNoMatch() {
        List<Expense> expenses = Arrays.asList(testExpense);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUser(testUser)).thenReturn(expenses);

        List<Expense> result = expenseService.searchExpenses("testuser", "Transport", null, null, null, null);

        assertEquals(0, result.size());
        verify(expenseRepository).findByUser(testUser);
    }

    @Test
    void testGetExpenseSumByCategory() {
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{"Food", new BigDecimal("100.00")});
        results.add(new Object[]{"Transport", new BigDecimal("50.00")});

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(expenseRepository.findExpenseSumByCategory(testUser)).thenReturn(results);

        Map<String, BigDecimal> result = expenseService.getExpenseSumByCategory("testuser");

        assertEquals(2, result.size());
        assertEquals(new BigDecimal("100.00"), result.get("Food"));
        assertEquals(new BigDecimal("50.00"), result.get("Transport"));
        verify(expenseRepository).findExpenseSumByCategory(testUser);
    }

    @Test
    void testGetExpenseSumByCategoryAndSubCategory() {
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{"Food", "Groceries", new BigDecimal("100.00")});
        results.add(new Object[]{"Food", null, new BigDecimal("50.00")});

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(expenseRepository.findExpenseSumByCategoryAndSubCategory(testUser)).thenReturn(results);

        Map<String, Map<String, BigDecimal>> result = expenseService.getExpenseSumByCategoryAndSubCategory("testuser");

        assertEquals(1, result.size());
        assertTrue(result.containsKey("Food"));
        assertEquals(2, result.get("Food").size());
        assertEquals(new BigDecimal("100.00"), result.get("Food").get("Groceries"));
        assertEquals(new BigDecimal("50.00"), result.get("Food").get("Uncategorized"));
        verify(expenseRepository).findExpenseSumByCategoryAndSubCategory(testUser);
    }

    @Test
    void testGetMonthlyExpenseSumByYear() {
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{1, new BigDecimal("100.00")});
        results.add(new Object[]{2, new BigDecimal("150.00")});

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(expenseRepository.findMonthlyExpenseSumByYear(testUser, 2024)).thenReturn(results);

        Map<Integer, BigDecimal> result = expenseService.getMonthlyExpenseSumByYear("testuser", 2024);

        assertEquals(2, result.size());
        assertEquals(new BigDecimal("100.00"), result.get(1));
        assertEquals(new BigDecimal("150.00"), result.get(2));
        verify(expenseRepository).findMonthlyExpenseSumByYear(testUser, 2024);
    }

    @Test
    void testGetExpenseSumByCard() {
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[]{"Visa", new BigDecimal("100.00")});
        results.add(new Object[]{null, new BigDecimal("50.00")});

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(expenseRepository.findExpenseSumByCard(testUser)).thenReturn(results);

        Map<String, BigDecimal> result = expenseService.getExpenseSumByCard("testuser");

        assertEquals(2, result.size());
        assertEquals(new BigDecimal("100.00"), result.get("Visa"));
        assertEquals(new BigDecimal("50.00"), result.get("Cash/Unknown"));
        verify(expenseRepository).findExpenseSumByCard(testUser);
    }
}
