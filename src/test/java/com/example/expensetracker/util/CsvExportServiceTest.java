package com.example.expensetracker.util;

import com.example.expensetracker.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvExportServiceTest {

    private CsvExportService csvExportService;
    private Expense testExpense1;
    private Expense testExpense2;

    @BeforeEach
    void setUp() {
        csvExportService = new CsvExportService();
        
        testExpense1 = new Expense();
        testExpense1.setId(1L);
        testExpense1.setName("Groceries");
        testExpense1.setAmount(new BigDecimal("100.00"));
        testExpense1.setDate(LocalDate.of(2024, 1, 15));
        testExpense1.setCategory("Food");
        testExpense1.setSubCategory("Supermarket");
        testExpense1.setLocation("Walmart");
        testExpense1.setCardUsed("Visa");

        testExpense2 = new Expense();
        testExpense2.setId(2L);
        testExpense2.setName("Gas");
        testExpense2.setAmount(new BigDecimal("50.00"));
        testExpense2.setDate(LocalDate.of(2024, 1, 20));
        testExpense2.setCategory("Transportation");
        testExpense2.setSubCategory("Fuel");
        testExpense2.setLocation("Shell");
        testExpense2.setCardUsed("Mastercard");
    }

    @Test
    void testExportExpensesToCsv() {
        List<Expense> expenses = Arrays.asList(testExpense1, testExpense2);
        
        byte[] csvContent = csvExportService.exportExpensesToCsv(expenses);
        
        assertNotNull(csvContent);
        assertTrue(csvContent.length > 0);
        
        String csvString = new String(csvContent);
        assertTrue(csvString.contains("ID,Name,Amount,Date,Category,Sub Category,Location,Card Used"));
        assertTrue(csvString.contains("1,Groceries,100.00,2024-01-15,Food,Supermarket,Walmart,Visa"));
        assertTrue(csvString.contains("2,Gas,50.00,2024-01-20,Transportation,Fuel,Shell,Mastercard"));
    }

    @Test
    void testExportEmptyList() {
        List<Expense> expenses = Collections.emptyList();
        
        byte[] csvContent = csvExportService.exportExpensesToCsv(expenses);
        
        assertNotNull(csvContent);
        assertTrue(csvContent.length > 0);
        
        String csvString = new String(csvContent);
        assertTrue(csvString.contains("ID,Name,Amount,Date,Category,Sub Category,Location,Card Used"));
    }

    @Test
    void testExportSingleExpense() {
        List<Expense> expenses = Collections.singletonList(testExpense1);
        
        byte[] csvContent = csvExportService.exportExpensesToCsv(expenses);
        
        assertNotNull(csvContent);
        assertTrue(csvContent.length > 0);
        
        String csvString = new String(csvContent);
        assertTrue(csvString.contains("ID,Name,Amount,Date,Category,Sub Category,Location,Card Used"));
        assertTrue(csvString.contains("1,Groceries,100.00,2024-01-15,Food,Supermarket,Walmart,Visa"));
    }

    @Test
    void testExportWithNullFields() {
        Expense expenseWithNulls = new Expense();
        expenseWithNulls.setId(3L);
        expenseWithNulls.setName("Test");
        expenseWithNulls.setAmount(new BigDecimal("25.00"));
        expenseWithNulls.setDate(LocalDate.of(2024, 2, 1));
        expenseWithNulls.setCategory("Other");
        
        List<Expense> expenses = Collections.singletonList(expenseWithNulls);
        
        byte[] csvContent = csvExportService.exportExpensesToCsv(expenses);
        
        assertNotNull(csvContent);
        assertTrue(csvContent.length > 0);
        
        String csvString = new String(csvContent);
        assertTrue(csvString.contains("3,Test,25.00,2024-02-01,Other"));
    }

    @Test
    void testExportWithSpecialCharacters() {
        Expense expenseWithSpecialChars = new Expense();
        expenseWithSpecialChars.setId(4L);
        expenseWithSpecialChars.setName("Coffee, Tea & Snacks");
        expenseWithSpecialChars.setAmount(new BigDecimal("15.50"));
        expenseWithSpecialChars.setDate(LocalDate.of(2024, 3, 1));
        expenseWithSpecialChars.setCategory("Food");
        expenseWithSpecialChars.setSubCategory("Café");
        expenseWithSpecialChars.setLocation("Starbucks \"Downtown\"");
        expenseWithSpecialChars.setCardUsed("Amex");
        
        List<Expense> expenses = Collections.singletonList(expenseWithSpecialChars);
        
        byte[] csvContent = csvExportService.exportExpensesToCsv(expenses);
        
        assertNotNull(csvContent);
        assertTrue(csvContent.length > 0);
        
        String csvString = new String(csvContent);
        assertTrue(csvString.contains("Coffee, Tea & Snacks"));
    }

    @Test
    void testExportMultipleExpenses() {
        Expense expense3 = new Expense();
        expense3.setId(3L);
        expense3.setName("Dinner");
        expense3.setAmount(new BigDecimal("75.00"));
        expense3.setDate(LocalDate.of(2024, 1, 25));
        expense3.setCategory("Food");
        expense3.setSubCategory("Restaurant");
        expense3.setLocation("Italian Place");
        expense3.setCardUsed("Visa");
        
        List<Expense> expenses = Arrays.asList(testExpense1, testExpense2, expense3);
        
        byte[] csvContent = csvExportService.exportExpensesToCsv(expenses);
        
        assertNotNull(csvContent);
        assertTrue(csvContent.length > 0);
        
        String csvString = new String(csvContent);
        assertTrue(csvString.contains("1,Groceries"));
        assertTrue(csvString.contains("2,Gas"));
        assertTrue(csvString.contains("3,Dinner"));
    }
}
