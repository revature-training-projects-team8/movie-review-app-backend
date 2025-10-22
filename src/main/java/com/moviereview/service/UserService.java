package com.moviereview.service;

import com.moviereview.exception.DuplicateResourceException;
import com.moviereview.model.User;
import com.moviereview.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists.");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Hash password with BCrypt
        user.setEmail(email);
        user.setRole("USER"); // Default role
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean validatePassword(User user, String rawPassword) {
        logger.debug("🔍 Validating password for user: {}", user.getUsername());
        logger.debug("📝 Stored hash starts with: {}", user.getPassword().substring(0, 10));
        logger.debug("📝 Raw password length: {}", rawPassword.length());

        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());

        logger.debug("✅ Password match result: {}", matches);
        return matches;
    }

    /**
     * Generate BCrypt hash for a given password
     * Used for testing and debugging password hashing
     */
    public String generateHash(String password) {
        return passwordEncoder.encode(password);
    }
}