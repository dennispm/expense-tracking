package com.example.expensetracker.model;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void testUserWithAllArgsConstructor() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        Set<Expense> expenses = new HashSet<>();
        
        User user = new User(1L, "testuser", "password123", "test@example.com", roles, expenses);
        
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(roles, user.getRoles());
        assertEquals(expenses, user.getExpenses());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();
        
        user.setId(1L);
        assertEquals(1L, user.getId());
        
        user.setUsername("testuser");
        assertEquals("testuser", user.getUsername());
        
        user.setPassword("password123");
        assertEquals("password123", user.getPassword());
        
        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());
        
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        assertEquals(roles, user.getRoles());
        
        Set<Expense> expenses = new HashSet<>();
        user.setExpenses(expenses);
        assertEquals(expenses, user.getExpenses());
    }

    @Test
    void testIsAdminWithAdminRole() {
        User user = new User();
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        user.setRoles(roles);
        
        assertTrue(user.isAdmin());
    }

    @Test
    void testIsAdminWithoutAdminRole() {
        User user = new User();
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        
        assertFalse(user.isAdmin());
    }

    @Test
    void testIsAdminWithEmptyRoles() {
        User user = new User();
        Set<String> roles = new HashSet<>();
        user.setRoles(roles);
        
        assertFalse(user.isAdmin());
    }

    @Test
    void testIsAdminWithMultipleRoles() {
        User user = new User();
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_MANAGER");
        user.setRoles(roles);
        
        assertTrue(user.isAdmin());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testuser");
        user1.setPassword("password123");
        user1.setEmail("test@example.com");
        
        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("testuser");
        user2.setPassword("password123");
        user2.setEmail("test@example.com");
        
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testNotEquals() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testuser1");
        
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testuser2");
        
        assertNotEquals(user1, user2);
    }

    @Test
    void testToString() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
    }

    @Test
    void testRolesInitialization() {
        User user = new User();
        assertNotNull(user.getRoles());
    }

    @Test
    void testExpensesInitialization() {
        User user = new User();
        assertNotNull(user.getExpenses());
    }
}
