package com.example.expensetracker.controller;

import com.example.expensetracker.model.PasswordEntry;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.UserRepository;
import com.example.expensetracker.service.PasswordEntryService;
import com.example.expensetracker.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/passwords")
@RequiredArgsConstructor
public class PasswordManagerController {

    private final PasswordEntryService passwordEntryService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String listPasswords(@RequestParam(required = false) String sortBy, 
                               Authentication authentication, 
                               Model model) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<PasswordEntry> entries = passwordEntryService.getAllPasswordEntries(user, sortBy);
        model.addAttribute("entries", entries);
        model.addAttribute("sortBy", sortBy != null ? sortBy : "");
        model.addAttribute("isAdmin", user.isAdmin());
        return "passwords/list";
    }

    @GetMapping("/new")
    public String showNewPasswordForm(Model model) {
        model.addAttribute("entry", new PasswordEntry());
        return "passwords/form";
    }

    @PostMapping("/save")
    public String savePassword(@ModelAttribute PasswordEntry entry,
                              @RequestParam String plainPassword,
                              @RequestParam String masterPassword,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!passwordEncoder.matches(masterPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Invalid master password");
                return "redirect:/passwords/new";
            }
            
            entry.setUser(user);
            passwordEntryService.createPasswordEntry(entry, plainPassword, masterPassword, user.getUsername());
            redirectAttributes.addFlashAttribute("success", "Password entry created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to save password: " + e.getMessage());
            return "redirect:/passwords/new";
        }
        return "redirect:/passwords";
    }

    @GetMapping("/edit/{id}")
    public String showEditPasswordForm(@PathVariable Long id, Model model, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        PasswordEntry entry = passwordEntryService.getPasswordEntryById(id)
                .orElseThrow(() -> new RuntimeException("Password entry not found"));
        
        if (!entry.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        model.addAttribute("entry", entry);
        return "passwords/edit";
    }

    @PostMapping("/update/{id}")
    public String updatePassword(@PathVariable Long id,
                                @ModelAttribute PasswordEntry entry,
                                @RequestParam(required = false) String plainPassword,
                                @RequestParam String masterPassword,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!passwordEncoder.matches(masterPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Invalid master password");
                return "redirect:/passwords/edit/" + id;
            }
            
            passwordEntryService.updatePasswordEntry(id, entry, plainPassword, masterPassword, user.getUsername());
            redirectAttributes.addFlashAttribute("success", "Password entry updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update password: " + e.getMessage());
            return "redirect:/passwords/edit/" + id;
        }
        return "redirect:/passwords";
    }

    @PostMapping("/delete/{id}")
    public String deletePassword(@PathVariable Long id, 
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            PasswordEntry entry = passwordEntryService.getPasswordEntryById(id)
                    .orElseThrow(() -> new RuntimeException("Password entry not found"));
            
            if (!entry.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized access");
            }
            
            passwordEntryService.deletePasswordEntry(id);
            redirectAttributes.addFlashAttribute("success", "Password entry deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete password: " + e.getMessage());
        }
        return "redirect:/passwords";
    }

    @GetMapping("/decrypt/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> decryptPassword(@PathVariable Long id,
                                                               @RequestParam String masterPassword,
                                                               Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!passwordEncoder.matches(masterPassword, user.getPassword())) {
                response.put("error", "Invalid master password");
                return ResponseEntity.badRequest().body(response);
            }
            
            PasswordEntry entry = passwordEntryService.getPasswordEntryById(id)
                    .orElseThrow(() -> new RuntimeException("Password entry not found"));
            
            if (!entry.getUser().getId().equals(user.getId())) {
                response.put("error", "Unauthorized access");
                return ResponseEntity.badRequest().body(response);
            }
            
            String plainPassword = passwordEntryService.decryptPassword(entry, masterPassword);
            response.put("password", plainPassword);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to decrypt password: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportPasswords(@RequestParam String masterPassword,
                                                  Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!passwordEncoder.matches(masterPassword, user.getPassword())) {
                return ResponseEntity.badRequest().build();
            }
            
            List<PasswordEntry> entries = passwordEntryService.getAllPasswordEntries(user, null);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(baos);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            writer.println("Password Manager Export");
            writer.println("=======================");
            writer.println();
            
            for (PasswordEntry entry : entries) {
                writer.println("Name: " + entry.getName());
                writer.println("Description: " + (entry.getDescription() != null ? entry.getDescription() : ""));
                writer.println("URL: " + (entry.getUrl() != null ? entry.getUrl() : ""));
                writer.println("Username: " + (entry.getUsername() != null ? entry.getUsername() : ""));
                writer.println("Email: " + (entry.getEmail() != null ? entry.getEmail() : ""));
                
                String plainPassword = passwordEntryService.decryptPassword(entry, masterPassword);
                writer.println("Password: " + plainPassword);
                
                writer.println("Date Created: " + entry.getDateCreated().format(formatter));
                writer.println("Date Last Modified: " + entry.getDateLastModified().format(formatter));
                writer.println("Modified By: " + entry.getModifiedBy());
                writer.println("---");
                writer.println();
            }
            
            writer.flush();
            writer.close();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "passwords_export.txt");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "passwords/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!user.isAdmin()) {
                redirectAttributes.addFlashAttribute("error", "Only admin can change password");
                return "redirect:/passwords";
            }
            
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
                return "redirect:/passwords/change-password";
            }
            
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "New passwords do not match");
                return "redirect:/passwords/change-password";
            }
            
            String validationError = PasswordValidator.validateAndGetError(newPassword);
            if (validationError != null) {
                redirectAttributes.addFlashAttribute("error", validationError);
                return "redirect:/passwords/change-password";
            }
            
            passwordEntryService.reencryptAllPasswords(user, currentPassword, newPassword);
            
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            redirectAttributes.addFlashAttribute("success", "Password changed successfully. All stored passwords have been re-encrypted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to change password: " + e.getMessage());
            return "redirect:/passwords/change-password";
        }
        return "redirect:/passwords";
    }
}
