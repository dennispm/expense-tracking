package com.example.expensetracker.controller;

import com.example.expensetracker.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @Test
    @WithMockUser(username = "testuser")
    void testHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testDashboard() throws Exception {
        Map<String, BigDecimal> categoryExpenses = new HashMap<>();
        categoryExpenses.put("Food", new BigDecimal("100.00"));
        categoryExpenses.put("Transportation", new BigDecimal("50.00"));

        Map<Integer, BigDecimal> monthlyExpenses = new HashMap<>();
        monthlyExpenses.put(1, new BigDecimal("200.00"));
        monthlyExpenses.put(2, new BigDecimal("150.00"));

        Map<String, BigDecimal> cardExpenses = new HashMap<>();
        cardExpenses.put("Visa", new BigDecimal("100.00"));
        cardExpenses.put("Mastercard", new BigDecimal("50.00"));

        when(expenseService.getExpenseSumByCategory("testuser")).thenReturn(categoryExpenses);
        when(expenseService.getMonthlyExpenseSumByYear(eq("testuser"), anyInt())).thenReturn(monthlyExpenses);
        when(expenseService.getExpenseSumByCard("testuser")).thenReturn(cardExpenses);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categoryExpenses"))
                .andExpect(model().attributeExists("monthlyExpenses"))
                .andExpect(model().attributeExists("cardExpenses"))
                .andExpect(model().attributeExists("totalExpenses"))
                .andExpect(model().attributeExists("currentYear"));

        verify(expenseService).getExpenseSumByCategory("testuser");
        verify(expenseService).getMonthlyExpenseSumByYear(eq("testuser"), anyInt());
        verify(expenseService).getExpenseSumByCard("testuser");
    }

}
