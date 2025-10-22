package com.moviereview.controller;

import com.moviereview.dto.LoginRequest;
import com.moviereview.dto.LoginResponse;
import com.moviereview.dto.UserRegistrationRequest;
import com.moviereview.model.User;
import com.moviereview.security.JwtUtil;
import com.moviereview.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Authentication Controller with JWT
 * Handles authentication endpoints at /auth/**
 * Provides login and registration functionality with JWT token generation
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * User login endpoint with JWT
     * POST /auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("🔐 Login attempt for username: {}", loginRequest.getUsername());

        try {
            // Authenticate user using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Fetch user details
            Optional<User> userOptional = userService.findByUsername(loginRequest.getUsername());
            if (userOptional.isEmpty()) {
                log.warn("❌ User not found after authentication: {}", loginRequest.getUsername());
                return ResponseEntity.status(401).body("Authentication failed");
            }

            User user = userOptional.get();
            log.info("✅ User authenticated: {} (email: {})", user.getUsername(), user.getEmail());

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            log.info("🎟️ JWT token generated for user: {}", user.getUsername());

            // Create response with JWT token
            LoginResponse response = new LoginResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    token);

            log.info("✅ Login successful for user: {}", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            log.warn("❌ Authentication failed for user: {} - {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    /**
     * User registration endpoint with JWT
     * POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            User newUser = userService.createUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getEmail());

            // Generate JWT token for the new user
            String token = jwtUtil.generateToken(newUser.getUsername(), newUser.getRole());
            log.info("🎟️ JWT token generated for new user: {}", newUser.getUsername());

            // Create response with JWT token
            LoginResponse response = new LoginResponse(
                    newUser.getId(),
                    newUser.getUsername(),
                    newUser.getEmail(),
                    newUser.getRole(),
                    token);

            log.info("✅ Registration successful for user: {}", newUser.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * TEST ENDPOINT - Generate BCrypt hash for a password
     * GET /auth/test/hash?password=yourpassword
     * This will help you generate the correct hash for your database
     */
    @GetMapping("/test/hash")
    public ResponseEntity<String> generateHash(@RequestParam String password) {
        String hash = userService.generateHash(password);
        log.info("🔐 Generated BCrypt hash for testing");
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

        log.info("🧪 Password verification test: {}", matches);
        return ResponseEntity.ok(result);
    }
}
