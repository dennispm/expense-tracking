package com.example.expensetracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private String url;

    private String username;

    private String email;

    @NotBlank(message = "Password is required")
    @Column(length = 1000)
    private String encryptedPassword;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private LocalDateTime dateLastModified;

    @Column(nullable = false)
    private String modifiedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDateTime.now();
        dateLastModified = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateLastModified = LocalDateTime.now();
    }
}
