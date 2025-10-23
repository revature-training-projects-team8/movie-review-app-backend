package com.moviereview.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * User entity representing registered users in the movie review system.
 * 
 * This entity handles user authentication and authorization. Users can have
 * either "USER" or "ADMIN" roles, where:
 * - USER: Can create, read, update, and delete their own reviews
 * - ADMIN: Has all USER permissions plus can manage movies and delete any review
 * 
 * Passwords are stored as BCrypt hashes for security.
 * Email addresses must be unique across all users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    
    /**
     * Primary key - auto-generated unique identifier for each user
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username for login and display purposes.
     * Must be unique across all users, 3-50 characters long.
     */
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /**
     * User's password stored as a BCrypt hash.
     * Never store plain text passwords! The UserService handles hashing.
     */
    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /**
     * User's email address - must be unique and valid format.
     * Used for account recovery and notifications (if implemented).
     */
    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * User role for authorization - either "USER" or "ADMIN".
     * Defaults to "USER" if not specified during registration.
     * ADMIN users have additional permissions for movie management.
     */
    @Column(length = 20)
    private String role = "USER";

    /**
     * Timestamp when the user account was created.
     * Automatically set when the entity is first persisted.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * One-to-many relationship with reviews written by this user.
     * When a user is deleted, all their reviews are also deleted (orphanRemoval = true).
     * JsonIgnore prevents infinite recursion during JSON serialization.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Review> reviews;

    /**
     * JPA lifecycle callback - automatically executed before persisting a new user.
     * Sets the creation timestamp and ensures role is set to default if null.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (role == null) {
            role = "USER";
        }
    }

}