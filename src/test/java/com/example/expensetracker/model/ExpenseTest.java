package com.example.expensetracker.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseTest {

    @Test
    void testExpenseCreation() {
        Expense expense = new Expense();
        assertNotNull(expense);
    }

    @Test
    void testExpenseWithAllArgsConstructor() {
        User user = new User();
        user.setId(1L);
        
        LocalDate date = LocalDate.now();
        BigDecimal amount = new BigDecimal("100.50");
        
        Expense expense = new Expense(1L, "Groceries", amount, date, "Food", 
                "Supermarket", "Store A", "Visa", user);
        
        assertEquals(1L, expense.getId());
        assertEquals("Groceries", expense.getName());
        assertEquals(amount, expense.getAmount());
        assertEquals(date, expense.getDate());
        assertEquals("Food", expense.getCategory());
        assertEquals("Supermarket", expense.getSubCategory());
        assertEquals("Store A", expense.getLocation());
        assertEquals("Visa", expense.getCardUsed());
        assertEquals(user, expense.getUser());
    }

    @Test
    void testSettersAndGetters() {
        Expense expense = new Expense();
        User user = new User();
        user.setId(1L);
        LocalDate date = LocalDate.now();
        BigDecimal amount = new BigDecimal("100.50");
        
        expense.setId(1L);
        assertEquals(1L, expense.getId());
        
        expense.setName("Groceries");
        assertEquals("Groceries", expense.getName());
        
        expense.setAmount(amount);
        assertEquals(amount, expense.getAmount());
        
        expense.setDate(date);
        assertEquals(date, expense.getDate());
        
        expense.setCategory("Food");
        assertEquals("Food", expense.getCategory());
        
        expense.setSubCategory("Supermarket");
        assertEquals("Supermarket", expense.getSubCategory());
        
        expense.setLocation("Store A");
        assertEquals("Store A", expense.getLocation());
        
        expense.setCardUsed("Visa");
        assertEquals("Visa", expense.getCardUsed());
        
        expense.setUser(user);
        assertEquals(user, expense.getUser());
    }

    @Test
    void testEqualsAndHashCode() {
        User user = new User();
        user.setId(1L);
        LocalDate date = LocalDate.now();
        BigDecimal amount = new BigDecimal("100.50");
        
        Expense expense1 = new Expense();
        expense1.setId(1L);
        expense1.setName("Groceries");
        expense1.setAmount(amount);
        expense1.setDate(date);
        expense1.setCategory("Food");
        expense1.setUser(user);
        
        Expense expense2 = new Expense();
        expense2.setId(1L);
        expense2.setName("Groceries");
        expense2.setAmount(amount);
        expense2.setDate(date);
        expense2.setCategory("Food");
        expense2.setUser(user);
        
        assertEquals(expense1, expense2);
        assertEquals(expense1.hashCode(), expense2.hashCode());
    }

    @Test
    void testNotEquals() {
        Expense expense1 = new Expense();
        expense1.setId(1L);
        expense1.setName("Groceries");
        
        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setName("Transport");
        
        assertNotEquals(expense1, expense2);
    }

    @Test
    void testToString() {
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setName("Groceries");
        expense.setAmount(new BigDecimal("100.50"));
        
        String toString = expense.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Groceries"));
    }

    @Test
    void testNullableFields() {
        Expense expense = new Expense();
        expense.setName("Test");
        expense.setAmount(new BigDecimal("100"));
        expense.setDate(LocalDate.now());
        expense.setCategory("Food");
        
        assertNull(expense.getSubCategory());
        assertNull(expense.getLocation());
        assertNull(expense.getCardUsed());
    }

    @Test
    void testAmountPrecision() {
        Expense expense = new Expense();
        BigDecimal amount = new BigDecimal("123.456");
        expense.setAmount(amount);
        
        assertEquals(amount, expense.getAmount());
        assertEquals(0, amount.compareTo(expense.getAmount()));
    }
}
