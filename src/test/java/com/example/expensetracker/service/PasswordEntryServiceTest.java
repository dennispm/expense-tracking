package com.example.expensetracker.service;

import com.example.expensetracker.model.PasswordEntry;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.PasswordEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordEntryServiceTest {

    @Mock
    private PasswordEntryRepository passwordEntryRepository;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private PasswordEntryService passwordEntryService;

    private User testUser;
    private PasswordEntry testEntry;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

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
    }

    @Test
    void testCreatePasswordEntry() throws Exception {
        String plainPassword = "TestPass123!";
        String masterPassword = "MasterPass123!";
        String encryptedPassword = "encryptedData";

        when(encryptionService.encrypt(plainPassword, masterPassword)).thenReturn(encryptedPassword);
        when(passwordEntryRepository.save(any(PasswordEntry.class))).thenReturn(testEntry);

        PasswordEntry result = passwordEntryService.createPasswordEntry(testEntry, plainPassword, masterPassword, "testuser");

        assertNotNull(result);
        verify(encryptionService).encrypt(plainPassword, masterPassword);
        verify(passwordEntryRepository).save(any(PasswordEntry.class));
    }

    @Test
    void testUpdatePasswordEntry() throws Exception {
        String plainPassword = "NewPass123!";
        String masterPassword = "MasterPass123!";
        String encryptedPassword = "newEncryptedData";

        PasswordEntry updatedEntry = new PasswordEntry();
        updatedEntry.setName("Updated Name");
        updatedEntry.setDescription("Updated Description");
        updatedEntry.setUrl("https://updated.com");
        updatedEntry.setUsername("updated@example.com");
        updatedEntry.setEmail("updated@example.com");

        when(passwordEntryRepository.findById(1L)).thenReturn(Optional.of(testEntry));
        when(encryptionService.encrypt(plainPassword, masterPassword)).thenReturn(encryptedPassword);
        when(passwordEntryRepository.save(any(PasswordEntry.class))).thenReturn(testEntry);

        PasswordEntry result = passwordEntryService.updatePasswordEntry(1L, updatedEntry, plainPassword, masterPassword, "testuser");

        assertNotNull(result);
        verify(passwordEntryRepository).findById(1L);
        verify(encryptionService).encrypt(plainPassword, masterPassword);
        verify(passwordEntryRepository).save(any(PasswordEntry.class));
    }

    @Test
    void testUpdatePasswordEntryWithoutChangingPassword() throws Exception {
        String masterPassword = "MasterPass123!";

        PasswordEntry updatedEntry = new PasswordEntry();
        updatedEntry.setName("Updated Name");
        updatedEntry.setDescription("Updated Description");

        when(passwordEntryRepository.findById(1L)).thenReturn(Optional.of(testEntry));
        when(passwordEntryRepository.save(any(PasswordEntry.class))).thenReturn(testEntry);

        PasswordEntry result = passwordEntryService.updatePasswordEntry(1L, updatedEntry, null, masterPassword, "testuser");

        assertNotNull(result);
        verify(passwordEntryRepository).findById(1L);
        verify(encryptionService, never()).encrypt(anyString(), anyString());
        verify(passwordEntryRepository).save(any(PasswordEntry.class));
    }

    @Test
    void testUpdatePasswordEntryNotFound() {
        when(passwordEntryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            passwordEntryService.updatePasswordEntry(1L, testEntry, "pass", "master", "user");
        });
    }

    @Test
    void testDeletePasswordEntry() {
        doNothing().when(passwordEntryRepository).deleteById(1L);

        passwordEntryService.deletePasswordEntry(1L);

        verify(passwordEntryRepository).deleteById(1L);
    }

    @Test
    void testGetAllPasswordEntriesNoSort() {
        List<PasswordEntry> entries = Arrays.asList(testEntry);
        when(passwordEntryRepository.findByUser(testUser)).thenReturn(entries);

        List<PasswordEntry> result = passwordEntryService.getAllPasswordEntries(testUser, null);

        assertEquals(1, result.size());
        verify(passwordEntryRepository).findByUser(testUser);
    }

    @Test
    void testGetAllPasswordEntriesSortByName() {
        List<PasswordEntry> entries = Arrays.asList(testEntry);
        when(passwordEntryRepository.findByUserOrderByNameAsc(testUser)).thenReturn(entries);

        List<PasswordEntry> result = passwordEntryService.getAllPasswordEntries(testUser, "name");

        assertEquals(1, result.size());
        verify(passwordEntryRepository).findByUserOrderByNameAsc(testUser);
    }

    @Test
    void testGetAllPasswordEntriesSortByUrl() {
        List<PasswordEntry> entries = Arrays.asList(testEntry);
        when(passwordEntryRepository.findByUserOrderByUrlAsc(testUser)).thenReturn(entries);

        List<PasswordEntry> result = passwordEntryService.getAllPasswordEntries(testUser, "url");

        assertEquals(1, result.size());
        verify(passwordEntryRepository).findByUserOrderByUrlAsc(testUser);
    }

    @Test
    void testGetAllPasswordEntriesSortByModifiedBy() {
        List<PasswordEntry> entries = Arrays.asList(testEntry);
        when(passwordEntryRepository.findByUserOrderByModifiedByAsc(testUser)).thenReturn(entries);

        List<PasswordEntry> result = passwordEntryService.getAllPasswordEntries(testUser, "modifiedBy");

        assertEquals(1, result.size());
        verify(passwordEntryRepository).findByUserOrderByModifiedByAsc(testUser);
    }

    @Test
    void testGetAllPasswordEntriesSortByDateCreated() {
        List<PasswordEntry> entries = Arrays.asList(testEntry);
        when(passwordEntryRepository.findByUserOrderByDateCreatedAsc(testUser)).thenReturn(entries);

        List<PasswordEntry> result = passwordEntryService.getAllPasswordEntries(testUser, "dateCreated");

        assertEquals(1, result.size());
        verify(passwordEntryRepository).findByUserOrderByDateCreatedAsc(testUser);
    }

    @Test
    void testGetAllPasswordEntriesSortByDateLastModified() {
        List<PasswordEntry> entries = Arrays.asList(testEntry);
        when(passwordEntryRepository.findByUserOrderByDateLastModifiedAsc(testUser)).thenReturn(entries);

        List<PasswordEntry> result = passwordEntryService.getAllPasswordEntries(testUser, "dateLastModified");

        assertEquals(1, result.size());
        verify(passwordEntryRepository).findByUserOrderByDateLastModifiedAsc(testUser);
    }

    @Test
    void testGetAllPasswordEntriesInvalidSort() {
        List<PasswordEntry> entries = Arrays.asList(testEntry);
        when(passwordEntryRepository.findByUser(testUser)).thenReturn(entries);

        List<PasswordEntry> result = passwordEntryService.getAllPasswordEntries(testUser, "invalid");

        assertEquals(1, result.size());
        verify(passwordEntryRepository).findByUser(testUser);
    }

    @Test
    void testGetPasswordEntryById() {
        when(passwordEntryRepository.findById(1L)).thenReturn(Optional.of(testEntry));

        Optional<PasswordEntry> result = passwordEntryService.getPasswordEntryById(1L);

        assertTrue(result.isPresent());
        assertEquals(testEntry, result.get());
        verify(passwordEntryRepository).findById(1L);
    }

    @Test
    void testGetPasswordEntryByIdNotFound() {
        when(passwordEntryRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<PasswordEntry> result = passwordEntryService.getPasswordEntryById(1L);

        assertFalse(result.isPresent());
        verify(passwordEntryRepository).findById(1L);
    }

    @Test
    void testDecryptPassword() throws Exception {
        String masterPassword = "MasterPass123!";
        String decryptedPassword = "DecryptedPass123!";

        when(encryptionService.decrypt(testEntry.getEncryptedPassword(), masterPassword)).thenReturn(decryptedPassword);

        String result = passwordEntryService.decryptPassword(testEntry, masterPassword);

        assertEquals(decryptedPassword, result);
        verify(encryptionService).decrypt(testEntry.getEncryptedPassword(), masterPassword);
    }

    @Test
    void testReencryptAllPasswords() throws Exception {
        String oldMasterPassword = "OldPass123!";
        String newMasterPassword = "NewPass123!";
        String decryptedPassword = "DecryptedPass123!";
        String newEncryptedPassword = "newEncryptedData";

        PasswordEntry entry1 = new PasswordEntry();
        entry1.setId(1L);
        entry1.setEncryptedPassword("encrypted1");

        PasswordEntry entry2 = new PasswordEntry();
        entry2.setId(2L);
        entry2.setEncryptedPassword("encrypted2");

        List<PasswordEntry> entries = Arrays.asList(entry1, entry2);

        when(passwordEntryRepository.findByUser(testUser)).thenReturn(entries);
        when(encryptionService.decrypt(anyString(), eq(oldMasterPassword))).thenReturn(decryptedPassword);
        when(encryptionService.encrypt(decryptedPassword, newMasterPassword)).thenReturn(newEncryptedPassword);
        when(passwordEntryRepository.save(any(PasswordEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        passwordEntryService.reencryptAllPasswords(testUser, oldMasterPassword, newMasterPassword);

        verify(passwordEntryRepository).findByUser(testUser);
        verify(encryptionService, times(2)).decrypt(anyString(), eq(oldMasterPassword));
        verify(encryptionService, times(2)).encrypt(decryptedPassword, newMasterPassword);
        verify(passwordEntryRepository, times(2)).save(any(PasswordEntry.class));
    }

    @Test
    void testReencryptAllPasswordsWithNoEntries() throws Exception {
        String oldMasterPassword = "OldPass123!";
        String newMasterPassword = "NewPass123!";

        when(passwordEntryRepository.findByUser(testUser)).thenReturn(Arrays.asList());

        passwordEntryService.reencryptAllPasswords(testUser, oldMasterPassword, newMasterPassword);

        verify(passwordEntryRepository).findByUser(testUser);
        verify(encryptionService, never()).decrypt(anyString(), anyString());
        verify(encryptionService, never()).encrypt(anyString(), anyString());
        verify(passwordEntryRepository, never()).save(any(PasswordEntry.class));
    }

    @Test
    void testReencryptAllPasswordsDecryptionFailure() throws Exception {
        String oldMasterPassword = "OldPass123!";
        String newMasterPassword = "NewPass123!";

        List<PasswordEntry> entries = Arrays.asList(testEntry);

        when(passwordEntryRepository.findByUser(testUser)).thenReturn(entries);
        when(encryptionService.decrypt(anyString(), eq(oldMasterPassword))).thenThrow(new RuntimeException("Decryption failed"));

        assertThrows(Exception.class, () -> {
            passwordEntryService.reencryptAllPasswords(testUser, oldMasterPassword, newMasterPassword);
        });

        verify(passwordEntryRepository).findByUser(testUser);
        verify(encryptionService).decrypt(anyString(), eq(oldMasterPassword));
        verify(encryptionService, never()).encrypt(anyString(), anyString());
    }
}
