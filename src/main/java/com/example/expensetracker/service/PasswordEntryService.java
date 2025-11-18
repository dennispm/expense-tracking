package com.example.expensetracker.service;

import com.example.expensetracker.model.PasswordEntry;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.PasswordEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordEntryService {

    private final PasswordEntryRepository passwordEntryRepository;
    private final EncryptionService encryptionService;

    @Transactional
    public PasswordEntry createPasswordEntry(PasswordEntry entry, String plainPassword, String masterPassword, String currentUsername) throws Exception {
        entry.setEncryptedPassword(encryptionService.encrypt(plainPassword, masterPassword));
        entry.setModifiedBy(currentUsername);
        return passwordEntryRepository.save(entry);
    }

    @Transactional
    public PasswordEntry updatePasswordEntry(Long id, PasswordEntry updatedEntry, String plainPassword, String masterPassword, String currentUsername) throws Exception {
        Optional<PasswordEntry> existingEntry = passwordEntryRepository.findById(id);
        if (existingEntry.isEmpty()) {
            throw new IllegalArgumentException("Password entry not found");
        }

        PasswordEntry entry = existingEntry.get();
        entry.setName(updatedEntry.getName());
        entry.setDescription(updatedEntry.getDescription());
        entry.setUrl(updatedEntry.getUrl());
        entry.setUsername(updatedEntry.getUsername());
        entry.setEmail(updatedEntry.getEmail());
        
        if (plainPassword != null && !plainPassword.isEmpty()) {
            entry.setEncryptedPassword(encryptionService.encrypt(plainPassword, masterPassword));
        }
        
        entry.setModifiedBy(currentUsername);
        return passwordEntryRepository.save(entry);
    }

    @Transactional
    public void deletePasswordEntry(Long id) {
        passwordEntryRepository.deleteById(id);
    }

    public List<PasswordEntry> getAllPasswordEntries(User user, String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return passwordEntryRepository.findByUser(user);
        }

        return switch (sortBy) {
            case "name" -> passwordEntryRepository.findByUserOrderByNameAsc(user);
            case "url" -> passwordEntryRepository.findByUserOrderByUrlAsc(user);
            case "modifiedBy" -> passwordEntryRepository.findByUserOrderByModifiedByAsc(user);
            case "dateCreated" -> passwordEntryRepository.findByUserOrderByDateCreatedAsc(user);
            case "dateLastModified" -> passwordEntryRepository.findByUserOrderByDateLastModifiedAsc(user);
            default -> passwordEntryRepository.findByUser(user);
        };
    }

    public Optional<PasswordEntry> getPasswordEntryById(Long id) {
        return passwordEntryRepository.findById(id);
    }

    public String decryptPassword(PasswordEntry entry, String masterPassword) throws Exception {
        return encryptionService.decrypt(entry.getEncryptedPassword(), masterPassword);
    }

    @Transactional
    public void reencryptAllPasswords(User user, String oldMasterPassword, String newMasterPassword) throws Exception {
        List<PasswordEntry> entries = passwordEntryRepository.findByUser(user);
        
        for (PasswordEntry entry : entries) {
            String plainPassword = encryptionService.decrypt(entry.getEncryptedPassword(), oldMasterPassword);
            entry.setEncryptedPassword(encryptionService.encrypt(plainPassword, newMasterPassword));
            passwordEntryRepository.save(entry);
        }
    }
}
