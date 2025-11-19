package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.ExpenseService;
import com.example.expensetracker.util.CsvExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private CsvExportService csvExportService;

    private Expense testExpense;

    @BeforeEach
    void setUp() {
        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setName("Groceries");
        testExpense.setAmount(new BigDecimal("100.00"));
        testExpense.setDate(LocalDate.now());
        testExpense.setCategory("Food");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllExpenses() throws Exception {
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseService.getExpensesByUser("testuser")).thenReturn(expenses);

        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(view().name("expenses/list"))
                .andExpect(model().attributeExists("expenses"))
                .andExpect(model().attributeExists("expense"));

        verify(expenseService).getExpensesByUser("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testShowAddForm() throws Exception {
        mockMvc.perform(get("/expenses/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("expenses/add"))
                .andExpect(model().attributeExists("expense"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testAddExpenseSuccess() throws Exception {
        when(expenseService.saveExpense(any(Expense.class), eq("testuser"))).thenReturn(testExpense);

        mockMvc.perform(post("/expenses/add")
                        .with(csrf())
                        .param("name", "Groceries")
                        .param("amount", "100.00")
                        .param("date", LocalDate.now().toString())
                        .param("category", "Food"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/expenses"));

        verify(expenseService).saveExpense(any(Expense.class), eq("testuser"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testAddExpenseValidationError() throws Exception {
        mockMvc.perform(post("/expenses/add")
                        .with(csrf())
                        .param("name", "")
                        .param("amount", "-100.00")
                        .param("date", "")
                        .param("category", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("expenses/add"));

        verify(expenseService, never()).saveExpense(any(Expense.class), anyString());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testShowSearchForm() throws Exception {
        mockMvc.perform(get("/expenses/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("expenses/search"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testSearchExpenses() throws Exception {
        List<Expense> searchResults = Arrays.asList(testExpense);
        when(expenseService.searchExpenses(eq("testuser"), anyString(), any(), any(), any(), anyString()))
                .thenReturn(searchResults);

        mockMvc.perform(post("/expenses/search")
                        .with(csrf())
                        .param("category", "Food")
                        .param("name", "Groceries"))
                .andExpect(status().isOk())
                .andExpect(view().name("expenses/search-results"))
                .andExpect(model().attributeExists("expenses"))
                .andExpect(model().attribute("searchPerformed", true));

        verify(expenseService).searchExpenses(eq("testuser"), anyString(), any(), any(), any(), anyString());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testSearchExpensesWithDateRange() throws Exception {
        List<Expense> searchResults = Arrays.asList(testExpense);
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        when(expenseService.searchExpenses(eq("testuser"), any(), any(LocalDate.class), any(LocalDate.class), any(), any()))
                .thenReturn(searchResults);

        mockMvc.perform(post("/expenses/search")
                        .with(csrf())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("expenses/search-results"));

        verify(expenseService).searchExpenses(eq("testuser"), any(), any(LocalDate.class), any(LocalDate.class), any(), any());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testExportExpenses() throws Exception {
        List<Expense> expenses = Arrays.asList(testExpense);
        byte[] csvContent = "Name,Amount,Date,Category\nGroceries,100.00,2024-01-01,Food\n".getBytes();
        
        when(expenseService.searchExpenses(eq("testuser"), any(), any(), any(), any(), any())).thenReturn(expenses);
        when(csvExportService.exportExpensesToCsv(expenses)).thenReturn(csvContent);

        mockMvc.perform(get("/expenses/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"expenses.csv\""));

        verify(expenseService).searchExpenses(eq("testuser"), any(), any(), any(), any(), any());
        verify(csvExportService).exportExpensesToCsv(expenses);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testExportExpensesWithFilters() throws Exception {
        List<Expense> expenses = Arrays.asList(testExpense);
        byte[] csvContent = "Name,Amount,Date,Category\nGroceries,100.00,2024-01-01,Food\n".getBytes();
        
        when(expenseService.searchExpenses(eq("testuser"), eq("Food"), any(), any(), any(), eq("Groceries")))
                .thenReturn(expenses);
        when(csvExportService.exportExpensesToCsv(expenses)).thenReturn(csvContent);

        mockMvc.perform(get("/expenses/export")
                        .param("category", "Food")
                        .param("name", "Groceries"))
                .andExpect(status().isOk());

        verify(expenseService).searchExpenses(eq("testuser"), eq("Food"), any(), any(), any(), eq("Groceries"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testAddExpenseApi() throws Exception {
        when(expenseService.saveExpense(any(Expense.class), eq("testuser"))).thenReturn(testExpense);

        mockMvc.perform(post("/expenses/api/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Groceries\",\"amount\":100.00,\"date\":\"2024-01-01\",\"category\":\"Food\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Groceries"));

        verify(expenseService).saveExpense(any(Expense.class), eq("testuser"));
    }

}
