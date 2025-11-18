package com.example.expensetracker.repository;

import com.example.expensetracker.model.PasswordEntry;
import com.example.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {
    List<PasswordEntry> findByUser(User user);
    List<PasswordEntry> findByUserOrderByNameAsc(User user);
    List<PasswordEntry> findByUserOrderByUrlAsc(User user);
    List<PasswordEntry> findByUserOrderByModifiedByAsc(User user);
    List<PasswordEntry> findByUserOrderByDateCreatedAsc(User user);
    List<PasswordEntry> findByUserOrderByDateLastModifiedAsc(User user);
}
