package com.moviereview.service;

import com.moviereview.config.TestSecurityConfig;
import com.moviereview.exception.DuplicateResourceException;
import com.moviereview.model.User;
import com.moviereview.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(TestSecurityConfig.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testCreateUser() {
        // Given
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setRole("USER");
        
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.createUser("testuser", "password123", "test@example.com");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("USER", result.getRole());
    }

    @Test
    void testCreateUserWithDuplicateUsername() {
        // Given
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            userService.createUser("existing", "password123", "test@example.com");
        });
    }

    @Test
    void testValidatePassword() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        String rawPassword = "password123";
        user.setPassword(passwordEncoder.encode(rawPassword));

        // When
        boolean result = userService.validatePassword(user, rawPassword);

        // Then
        assertTrue(result);
    }

    @Test
    void testFindByUsername() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }
}