package com.example.expensetracker.controller;

import com.example.expensetracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ExpenseService expenseService;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        int currentYear = LocalDate.now().getYear();
        
        // Get expense statistics
        Map<String, BigDecimal> categoryExpenses = expenseService.getExpenseSumByCategory(username);
        Map<Integer, BigDecimal> monthlyExpenses = expenseService.getMonthlyExpenseSumByYear(username, currentYear);
        Map<String, BigDecimal> cardExpenses = expenseService.getExpenseSumByCard(username);
        
        // Calculate total expenses
        BigDecimal totalExpenses = categoryExpenses.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Add data to model
        model.addAttribute("categoryExpenses", categoryExpenses);
        model.addAttribute("monthlyExpenses", monthlyExpenses);
        model.addAttribute("cardExpenses", cardExpenses);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("currentYear", currentYear);
        
        return "dashboard";
    }

    @GetMapping("/dashboard/year")
    public String dashboardByYear(@RequestParam int year, Model model, Authentication authentication) {
        String username = authentication.getName();
        
        // Get expense statistics for the specified year
        Map<Integer, BigDecimal> monthlyExpenses = expenseService.getMonthlyExpenseSumByYear(username, year);
        
        // Calculate total expenses for the year
        BigDecimal totalYearlyExpenses = monthlyExpenses.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Add data to model
        model.addAttribute("monthlyExpenses", monthlyExpenses);
        model.addAttribute("totalYearlyExpenses", totalYearlyExpenses);
        model.addAttribute("selectedYear", year);
        
        return "dashboard-year";
    }
}
