package com.moviereview.service;

import com.moviereview.exception.DuplicateResourceException;
import com.moviereview.model.User;
import com.moviereview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Service layer for managing user operations.
 * 
 * Handles:
 * - User registration with password hashing
 * - User authentication and password validation
 * - User lookup operations
 * - Role management (USER vs ADMIN)
 * 
 * Security Features:
 * - All passwords are hashed using BCrypt before storage
 * - Username uniqueness is enforced
 * - Default role assignment for security
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user with default USER role.
     * Convenience method that delegates to the full createUser method.
     * 
     * @param username Unique username for the new user
     * @param password Plain text password (will be hashed)
     * @param email User's email address
     * @return The created and saved user entity
     * @throws DuplicateResourceException if username already exists
     */
    public User createUser(String username, String password, String email) {
        return createUser(username, password, email, "USER");
    }

    /**
     * Creates a new user with specified role.
     * 
     * Business Rules:
     * - Usernames must be unique across the system
     * - Passwords are automatically hashed using BCrypt
     * - Role defaults to "USER" if null or empty
     * - Valid roles are "USER" and "ADMIN"
     * 
     * @param username Unique username for the new user
     * @param password Plain text password (will be hashed)
     * @param email User's email address
     * @param role User role ("USER" or "ADMIN")
     * @return The created and saved user entity
     * @throws DuplicateResourceException if username already exists
     */
    public User createUser(String username, String password, String email, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists.");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Hash password with BCrypt
        user.setEmail(email);
        user.setRole(role != null && !role.trim().isEmpty() ? role : "USER"); // Default role
        return userRepository.save(user);
    }

    /**
     * Finds a user by their username.
     * Used primarily during authentication and JWT token validation.
     * 
     * @param username The username to search for
     * @return Optional containing the user if found, empty if not found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by their ID.
     * Used for user profile operations and relationship mapping.
     * 
     * @param id The user ID to search for
     * @return Optional containing the user if found, empty if not found
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Validates a plain text password against a user's stored hash.
     * Used during authentication to verify login credentials.
     * 
     * @param user The user entity containing the hashed password
     * @param rawPassword The plain text password to validate
     * @return true if password matches, false otherwise
     */
    public boolean validatePassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}