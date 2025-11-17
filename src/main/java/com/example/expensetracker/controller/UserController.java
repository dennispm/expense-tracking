package com.example.expensetracker.controller;

import com.example.expensetracker.model.User;
import com.example.expensetracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        try {
            userService.createUser(user, false);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            result.rejectValue("username", "error.user", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/add-user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/add")
    public String addUser(@Valid @ModelAttribute("user") User user, 
                         BindingResult result, 
                         @RequestParam(defaultValue = "false") boolean isAdmin) {
        if (result.hasErrors()) {
            return "admin/add-user";
        }
        
        try {
            userService.createUser(user, isAdmin);
            return "redirect:/admin/users";
        } catch (RuntimeException e) {
            result.rejectValue("username", "error.user", e.getMessage());
            return "admin/add-user";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserByUsername(id.toString())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "admin/edit-user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/{id}/edit")
    public String updateUser(@PathVariable Long id, 
                            @Valid @ModelAttribute("user") User user, 
                            BindingResult result) {
        if (result.hasErrors()) {
            return "admin/edit-user";
        }
        
        try {
            userService.updateUser(user);
            return "redirect:/admin/users";
        } catch (RuntimeException e) {
            result.rejectValue("username", "error.user", e.getMessage());
            return "admin/edit-user";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/{id}/grant-admin")
    public String grantAdminRole(@PathVariable Long id) {
        userService.grantAdminRole(id);
        return "redirect:/admin/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/{id}/revoke-admin")
    public String revokeAdminRole(@PathVariable Long id) {
        userService.revokeAdminRole(id);
        return "redirect:/admin/users";
    }
}
