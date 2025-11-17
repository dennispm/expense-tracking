package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.ExpenseService;
import com.example.expensetracker.util.CsvExportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CsvExportService csvExportService;

    @GetMapping
    public String getAllExpenses(Model model, Authentication authentication) {
        List<Expense> expenses = expenseService.getExpensesByUser(authentication.getName());
        model.addAttribute("expenses", expenses);
        model.addAttribute("expense", new Expense());
        return "expenses/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "expenses/add";
    }

    @PostMapping("/add")
    public String addExpense(@Valid @ModelAttribute("expense") Expense expense, 
                            BindingResult result, Authentication authentication) {
        if (result.hasErrors()) {
            return "expenses/add";
        }
        
        expenseService.saveExpense(expense, authentication.getName());
        return "redirect:/expenses";
    }

    @GetMapping("/search")
    public String showSearchForm(Model model) {
        model.addAttribute("categories", List.of("Food", "Transportation", "Housing", "Entertainment", "Utilities", "Healthcare", "Other"));
        return "expenses/search";
    }

    @PostMapping("/search")
    public String searchExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) String name,
            Model model, Authentication authentication) {
        
        List<Expense> searchResults = expenseService.searchExpenses(
                authentication.getName(), category, startDate, endDate, minAmount, name);
        
        model.addAttribute("expenses", searchResults);
        model.addAttribute("searchPerformed", true);
        return "expenses/search-results";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) String name,
            Authentication authentication) {
        
        List<Expense> expenses = expenseService.searchExpenses(
                authentication.getName(), category, startDate, endDate, minAmount, name);
        
        byte[] csvContent = csvExportService.exportExpensesToCsv(expenses);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "expenses.csv");
        
        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }

    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<Expense> addExpenseApi(@Valid @RequestBody Expense expense, 
                                               Authentication authentication) {
        Expense savedExpense = expenseService.saveExpense(expense, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public String getAllUsersExpenses(Model model) {
        List<Expense> allExpenses = expenseService.getAllExpenses();
        model.addAttribute("expenses", allExpenses);
        return "expenses/admin-view";
    }
}
