package com.example.expensetracker.controller;

import com.example.expensetracker.model.PasswordEntry;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.UserRepository;
import com.example.expensetracker.service.PasswordEntryService;
import com.example.expensetracker.util.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordManagerController.class)
class PasswordManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordEntryService passwordEntryService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private PasswordEntry testEntry;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(new java.util.HashSet<>());

        testEntry = new PasswordEntry();
        testEntry.setId(1L);
        testEntry.setName("Test Entry");
        testEntry.setDescription("Test Description");
        testEntry.setUrl("https://example.com");
        testEntry.setUsername("user@example.com");
        testEntry.setEmail("user@example.com");
        testEntry.setEncryptedPassword("encrypted123");
        testEntry.setModifiedBy("testuser");
        testEntry.setUser(testUser);
        testEntry.setDateCreated(LocalDateTime.now());
        testEntry.setDateLastModified(LocalDateTime.now());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testListPasswords() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEntryService.getAllPasswordEntries(testUser, null)).thenReturn(Arrays.asList(testEntry));

        mockMvc.perform(get("/passwords"))
                .andExpect(status().isOk())
                .andExpect(view().name("passwords/list"))
                .andExpect(model().attributeExists("entries"))
                .andExpect(model().attribute("sortBy", ""));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testListPasswordsWithSort() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEntryService.getAllPasswordEntries(testUser, "name")).thenReturn(Arrays.asList(testEntry));

        mockMvc.perform(get("/passwords").param("sortBy", "name"))
                .andExpect(status().isOk())
                .andExpect(view().name("passwords/list"))
                .andExpect(model().attribute("sortBy", "name"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testShowNewPasswordForm() throws Exception {
        mockMvc.perform(get("/passwords/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("passwords/form"))
                .andExpect(model().attributeExists("entry"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testSavePasswordSuccess() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("masterPass", "encodedPassword")).thenReturn(true);
        when(passwordEntryService.createPasswordEntry(any(), anyString(), anyString(), anyString())).thenReturn(testEntry);

        mockMvc.perform(post("/passwords/save")
                        .with(csrf())
                        .param("name", "Test Entry")
                        .param("plainPassword", "TestPass123!")
                        .param("masterPassword", "masterPass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/passwords"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testSavePasswordInvalidMasterPassword() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        mockMvc.perform(post("/passwords/save")
                        .with(csrf())
                        .param("name", "Test Entry")
                        .param("plainPassword", "TestPass123!")
                        .param("masterPassword", "wrongPass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/passwords/new"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testSavePasswordException() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("masterPass", "encodedPassword")).thenReturn(true);
        when(passwordEntryService.createPasswordEntry(any(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Encryption failed"));

        mockMvc.perform(post("/passwords/save")
                        .with(csrf())
                        .param("name", "Test Entry")
                        .param("plainPassword", "TestPass123!")
                        .param("masterPassword", "masterPass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/passwords/new"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testShowEditPasswordForm() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEntryService.getPasswordEntryById(1L)).thenReturn(Optional.of(testEntry));

        mockMvc.perform(get("/passwords/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("passwords/edit"))
                .andExpect(model().attributeExists("entry"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testShowEditPasswordFormUnauthorized() throws Exception {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otheruser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        PasswordEntry otherEntry = new PasswordEntry();
        otherEntry.setId(2L);
        otherEntry.setUser(otherUser);
        
        when(passwordEntryService.getPasswordEntryById(2L)).thenReturn(Optional.of(otherEntry));

        try {
            mockMvc.perform(get("/passwords/edit/2"));
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof RuntimeException);
            assertTrue(e.getCause().getMessage().contains("Unauthorized"));
        }
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdatePasswordSuccess() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("masterPass", "encodedPassword")).thenReturn(true);
        when(passwordEntryService.updatePasswordEntry(eq(1L), any(), anyString(), anyString(), anyString())).thenReturn(testEntry);

        mockMvc.perform(post("/passwords/update/1")
                        .with(csrf())
                        .param("name", "Updated Entry")
                        .param("plainPassword", "NewPass123!")
                        .param("masterPassword", "masterPass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/passwords"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdatePasswordInvalidMasterPassword() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        mockMvc.perform(post("/passwords/update/1")
                        .with(csrf())
                        .param("name", "Updated Entry")
                        .param("masterPassword", "wrongPass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/passwords/edit/1"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testDeletePasswordSuccess() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEntryService.getPasswordEntryById(1L)).thenReturn(Optional.of(testEntry));
        doNothing().when(passwordEntryService).deletePasswordEntry(1L);

        mockMvc.perform(post("/passwords/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/passwords"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testDeletePasswordUnauthorized() throws Exception {
        User otherUser = new User();
        otherUser.setId(2L);
        
        PasswordEntry otherEntry = new PasswordEntry();
        otherEntry.setId(2L);
        otherEntry.setUser(otherUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEntryService.getPasswordEntryById(2L)).thenReturn(Optional.of(otherEntry));

        mockMvc.perform(post("/passwords/delete/2").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/passwords"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testDecryptPasswordSuccess() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("masterPass", "encodedPassword")).thenReturn(true);
        when(passwordEntryService.getPasswordEntryById(1L)).thenReturn(Optional.of(testEntry));
        when(passwordEntryService.decryptPassword(testEntry, "masterPass")).thenReturn("DecryptedPass123!");

        mockMvc.perform(get("/passwords/decrypt/1").param("masterPassword", "masterPass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").value("DecryptedPass123!"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testDecryptPasswordInvalidMasterPassword() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        mockMvc.perform(get("/passwords/decrypt/1").param("masterPassword", "wrongPass"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid master password"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testDecryptPasswordUnauthorized() throws Exception {
        User otherUser = new User();
        otherUser.setId(2L);
        
        PasswordEntry otherEntry = new PasswordEntry();
        otherEntry.setId(2L);
        otherEntry.setUser(otherUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("masterPass", "encodedPassword")).thenReturn(true);
        when(passwordEntryService.getPasswordEntryById(2L)).thenReturn(Optional.of(otherEntry));

        mockMvc.perform(get("/passwords/decrypt/2").param("masterPassword", "masterPass"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unauthorized access"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testExportPasswordsSuccess() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("masterPass", "encodedPassword")).thenReturn(true);
        when(passwordEntryService.getAllPasswordEntries(testUser, null)).thenReturn(Arrays.asList(testEntry));
        when(passwordEntryService.decryptPassword(testEntry, "masterPass")).thenReturn("DecryptedPass123!");

        mockMvc.perform(get("/passwords/export").param("masterPassword", "masterPass"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"passwords_export.txt\""));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testExportPasswordsInvalidMasterPassword() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        mockMvc.perform(get("/passwords/export").param("masterPassword", "wrongPass"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowChangePasswordForm() throws Exception {
        mockMvc.perform(get("/passwords/change-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("passwords/change-password"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testChangePasswordSuccess() throws Exception {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword("encodedOldPassword");
        java.util.Set<String> roles = new java.util.HashSet<>();
        roles.add("ROLE_ADMIN");
        adminUser.setRoles(roles);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("OldPass123!", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("NewPass123!")).thenReturn("encodedNewPassword");
        doNothing().when(passwordEntryService).reencryptAllPasswords(adminUser, "OldPass123!", "NewPass123!");
        when(userRepository.save(adminUser)).thenReturn(adminUser);

        mockMvc.perform(post("/passwords/change-password")
                        .with(csrf())
                        .param("currentPassword", "OldPass123!")
                        .param("newPassword", "NewPass123!")
                        .param("confirmPassword", "NewPass123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/passwords"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testChangePasswordNonAdmin() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/passwords/change-password")
                        .with(csrf())
                        .param("currentPassword", "OldPass123!")
                        .param("newPassword", "NewPass123!")
                        .param("confirmPassword", "NewPass123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/passwords"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testChangePasswordIncorrectCurrentPassword() throws Exception {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword("encodedOldPassword");
        java.util.Set<String> roles = new java.util.HashSet<>();
        roles.add("ROLE_ADMIN");
        adminUser.setRoles(roles);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("WrongPass!", "encodedOldPassword")).thenReturn(false);

        mockMvc.perform(post("/passwords/change-password")
                        .with(csrf())
                        .param("currentPassword", "WrongPass!")
                        .param("newPassword", "NewPass123!")
                        .param("confirmPassword", "NewPass123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/passwords/change-password"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testChangePasswordMismatch() throws Exception {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword("encodedOldPassword");
        java.util.Set<String> roles = new java.util.HashSet<>();
        roles.add("ROLE_ADMIN");
        adminUser.setRoles(roles);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("OldPass123!", "encodedOldPassword")).thenReturn(true);

        mockMvc.perform(post("/passwords/change-password")
                        .with(csrf())
                        .param("currentPassword", "OldPass123!")
                        .param("newPassword", "NewPass123!")
                        .param("confirmPassword", "DifferentPass123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/passwords/change-password"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testChangePasswordInvalidNewPassword() throws Exception {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setPassword("encodedOldPassword");
        java.util.Set<String> roles = new java.util.HashSet<>();
        roles.add("ROLE_ADMIN");
        adminUser.setRoles(roles);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("OldPass123!", "encodedOldPassword")).thenReturn(true);

        mockMvc.perform(post("/passwords/change-password")
                        .with(csrf())
                        .param("currentPassword", "OldPass123!")
                        .param("newPassword", "weak")
                        .param("confirmPassword", "weak"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/passwords/change-password"));
    }
}
