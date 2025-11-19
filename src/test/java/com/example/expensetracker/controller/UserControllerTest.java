package com.example.expensetracker.controller;

import com.example.expensetracker.model.User;
import com.example.expensetracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setRoles(new HashSet<>());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testListUsers() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"));

        verify(userService).getAllUsers();
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(post("/admin/users/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGrantAdminRole() throws Exception {
        doNothing().when(userService).grantAdminRole(1L);

        mockMvc.perform(post("/admin/users/1/grant-admin").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).grantAdminRole(1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRevokeAdminRole() throws Exception {
        doNothing().when(userService).revokeAdminRole(1L);

        mockMvc.perform(post("/admin/users/1/revoke-admin").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).revokeAdminRole(1L);
    }
}
