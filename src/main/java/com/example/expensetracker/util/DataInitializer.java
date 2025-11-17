package com.example.expensetracker.util;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));
            userRepository.save(admin);
            
            // Add some sample expenses for admin
            createSampleExpenses(admin);
        }

        // Create regular user
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            user.setRoles(Set.of("ROLE_USER"));
            userRepository.save(user);
            
            // Add some sample expenses for user
            createSampleExpenses(user);
        }
    }

    private void createSampleExpenses(User user) {
        // Groceries
        Expense groceries = new Expense();
        groceries.setName("Weekly Groceries");
        groceries.setAmount(new BigDecimal("85.75"));
        groceries.setDate(LocalDate.now().minusDays(2));
        groceries.setCategory("Food");
        groceries.setSubCategory("Groceries");
        groceries.setLocation("Whole Foods");
        groceries.setCardUsed("Visa");
        groceries.setUser(user);
        expenseRepository.save(groceries);

        // Restaurant
        Expense restaurant = new Expense();
        restaurant.setName("Dinner with Friends");
        restaurant.setAmount(new BigDecimal("45.50"));
        restaurant.setDate(LocalDate.now().minusDays(5));
        restaurant.setCategory("Food");
        restaurant.setSubCategory("Restaurant");
        restaurant.setLocation("Italian Bistro");
        restaurant.setCardUsed("Mastercard");
        restaurant.setUser(user);
        expenseRepository.save(restaurant);

        // Utilities
        Expense utilities = new Expense();
        utilities.setName("Electricity Bill");
        utilities.setAmount(new BigDecimal("120.00"));
        utilities.setDate(LocalDate.now().minusDays(10));
        utilities.setCategory("Utilities");
        utilities.setSubCategory("Electricity");
        utilities.setLocation("Home");
        utilities.setCardUsed("Bank Transfer");
        utilities.setUser(user);
        expenseRepository.save(utilities);

        // Transportation
        Expense transportation = new Expense();
        transportation.setName("Gas");
        transportation.setAmount(new BigDecimal("35.25"));
        transportation.setDate(LocalDate.now().minusDays(3));
        transportation.setCategory("Transportation");
        transportation.setSubCategory("Fuel");
        transportation.setLocation("Shell Gas Station");
        transportation.setCardUsed("Amex");
        transportation.setUser(user);
        expenseRepository.save(transportation);

        // Entertainment
        Expense entertainment = new Expense();
        entertainment.setName("Movie Tickets");
        entertainment.setAmount(new BigDecimal("24.00"));
        entertainment.setDate(LocalDate.now().minusDays(7));
        entertainment.setCategory("Entertainment");
        entertainment.setSubCategory("Movies");
        entertainment.setLocation("AMC Theater");
        entertainment.setCardUsed("Visa");
        entertainment.setUser(user);
        expenseRepository.save(entertainment);
    }
}
