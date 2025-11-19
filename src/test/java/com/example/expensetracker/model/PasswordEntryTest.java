package com.example.expensetracker.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEntryTest {

    @Test
    void testPasswordEntryCreation() {
        PasswordEntry entry = new PasswordEntry();
        assertNotNull(entry);
    }

    @Test
    void testPasswordEntryWithAllArgsConstructor() {
        User user = new User();
        user.setId(1L);
        
        LocalDateTime now = LocalDateTime.now();
        PasswordEntry entry = new PasswordEntry(1L, "Test Entry", "Description", "https://example.com",
                "username", "email@example.com", "encrypted", now, now, "testuser", user);
        
        assertEquals(1L, entry.getId());
        assertEquals("Test Entry", entry.getName());
        assertEquals("Description", entry.getDescription());
        assertEquals("https://example.com", entry.getUrl());
        assertEquals("username", entry.getUsername());
        assertEquals("email@example.com", entry.getEmail());
        assertEquals("encrypted", entry.getEncryptedPassword());
        assertEquals(now, entry.getDateCreated());
        assertEquals(now, entry.getDateLastModified());
        assertEquals("testuser", entry.getModifiedBy());
        assertEquals(user, entry.getUser());
    }

    @Test
    void testSettersAndGetters() {
        PasswordEntry entry = new PasswordEntry();
        User user = new User();
        user.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        
        entry.setId(1L);
        assertEquals(1L, entry.getId());
        
        entry.setName("Test Entry");
        assertEquals("Test Entry", entry.getName());
        
        entry.setDescription("Test Description");
        assertEquals("Test Description", entry.getDescription());
        
        entry.setUrl("https://example.com");
        assertEquals("https://example.com", entry.getUrl());
        
        entry.setUsername("testuser");
        assertEquals("testuser", entry.getUsername());
        
        entry.setEmail("test@example.com");
        assertEquals("test@example.com", entry.getEmail());
        
        entry.setEncryptedPassword("encrypted123");
        assertEquals("encrypted123", entry.getEncryptedPassword());
        
        entry.setDateCreated(now);
        assertEquals(now, entry.getDateCreated());
        
        entry.setDateLastModified(now);
        assertEquals(now, entry.getDateLastModified());
        
        entry.setModifiedBy("testuser");
        assertEquals("testuser", entry.getModifiedBy());
        
        entry.setUser(user);
        assertEquals(user, entry.getUser());
    }

    @Test
    void testEqualsAndHashCode() {
        User user = new User();
        user.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        
        PasswordEntry entry1 = new PasswordEntry();
        entry1.setId(1L);
        entry1.setName("Test Entry");
        entry1.setEncryptedPassword("encrypted");
        entry1.setUser(user);
        entry1.setDateCreated(now);
        entry1.setDateLastModified(now);
        entry1.setModifiedBy("testuser");
        
        PasswordEntry entry2 = new PasswordEntry();
        entry2.setId(1L);
        entry2.setName("Test Entry");
        entry2.setEncryptedPassword("encrypted");
        entry2.setUser(user);
        entry2.setDateCreated(now);
        entry2.setDateLastModified(now);
        entry2.setModifiedBy("testuser");
        
        assertEquals(entry1, entry2);
        assertEquals(entry1.hashCode(), entry2.hashCode());
    }

    @Test
    void testNotEquals() {
        PasswordEntry entry1 = new PasswordEntry();
        entry1.setId(1L);
        entry1.setName("Entry 1");
        
        PasswordEntry entry2 = new PasswordEntry();
        entry2.setId(2L);
        entry2.setName("Entry 2");
        
        assertNotEquals(entry1, entry2);
    }

    @Test
    void testToString() {
        PasswordEntry entry = new PasswordEntry();
        entry.setId(1L);
        entry.setName("Test Entry");
        entry.setUrl("https://example.com");
        
        String toString = entry.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Test Entry"));
    }

    @Test
    void testNullableFields() {
        PasswordEntry entry = new PasswordEntry();
        entry.setName("Test");
        entry.setEncryptedPassword("encrypted");
        
        assertNull(entry.getDescription());
        assertNull(entry.getUrl());
        assertNull(entry.getUsername());
        assertNull(entry.getEmail());
    }
}
