package com.moviereview.controller;

import com.moviereview.dto.LoginRequest;
import com.moviereview.dto.LoginResponse;
import com.moviereview.dto.UserRegistrationRequest;
import com.moviereview.model.User;
import com.moviereview.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Authentication Controller
 * Handles authentication endpoints at /auth/**
 * Provides login and registration functionality
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * User login endpoint
     * POST /auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("🔐 Login attempt for username: {}", loginRequest.getUsername());

        Optional<User> userOptional = userService.findByUsername(loginRequest.getUsername());

        if (userOptional.isEmpty()) {
            logger.warn("❌ User not found: {}", loginRequest.getUsername());
            return ResponseEntity.status(401).body(null);
        }

        User user = userOptional.get();
        logger.info("✅ User found: {} (email: {})", user.getUsername(), user.getEmail());

        // Validate password using BCrypt
        boolean passwordValid = userService.validatePassword(user, loginRequest.getPassword());
        logger.info("🔑 Password validation result: {}", passwordValid);

        if (passwordValid) {
            LoginResponse response = new LoginResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole());
            logger.info("✅ Login successful for user: {}", user.getUsername());
            return ResponseEntity.ok(response);
        }

        logger.warn("❌ Invalid password for user: {}", loginRequest.getUsername());
        return ResponseEntity.status(401).body(null);
    }

    /**
     * User registration endpoint
     * POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationRequest request) {
        User newUser = userService.createUser(
                request.getUsername(),
                request.getPassword(),
                request.getEmail());
        return ResponseEntity.ok(newUser);
    }

    /**
     * TEST ENDPOINT - Generate BCrypt hash for a password
     * GET /auth/test/hash?password=yourpassword
     * This will help you generate the correct hash for your database
     */
    @GetMapping("/test/hash")
    public ResponseEntity<String> generateHash(@RequestParam String password) {
        String hash = userService.generateHash(password);
        logger.info("🔐 Generated BCrypt hash for testing");
        return ResponseEntity.ok("BCrypt hash: " + hash);
    }

    /**
     * TEST ENDPOINT - Test if a password matches the admin's stored hash
     * GET /auth/test/verify?password=admin123
     */
    @GetMapping("/test/verify")
    public ResponseEntity<String> testAdminPassword(@RequestParam String password) {
        Optional<User> adminUser = userService.findByUsername("admin");

        if (adminUser.isEmpty()) {
            return ResponseEntity.ok("❌ Admin user not found in database");
        }

        User user = adminUser.get();
        boolean matches = userService.validatePassword(user, password);

        String result = String.format(
                "Testing password: '%s'\n" +
                        "Stored hash: %s\n" +
                        "Password matches: %s\n\n" +
                        "%s",
                password,
                user.getPassword(),
                matches ? "✅ YES" : "❌ NO",
                matches ? "Login should work!" : "This is why login fails - hash mismatch!");

        logger.info("🧪 Password verification test: {}", matches);
        return ResponseEntity.ok(result);
    }
}
