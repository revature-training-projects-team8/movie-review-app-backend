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
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Authentication Controller with JWT ##
 * Handles authentication endpoints at /auth/**
 * Provides login and registration functionality with JWT token generation
 */
@RestController
@RequestMapping("/auth")
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
        authenticationManager.authenticate(
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
                    request.getEmail(),
                    request.getRole());

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

}
