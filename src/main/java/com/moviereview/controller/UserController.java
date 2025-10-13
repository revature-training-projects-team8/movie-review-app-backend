package com.moviereview.controller;

import com.moviereview.exception.DuplicateResourceException;
import com.moviereview.exception.ResourceNotFoundException;
import com.moviereview.model.User;
import com.moviereview.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" })
public class UserController {

    @Autowired
    private UserService userService;

    // Create a new user (simplified registration)
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        try {
            User savedUser = userService.createUser(user.getUsername(), user.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (RuntimeException e) {
            throw new DuplicateResourceException(e.getMessage());
        }
    }

    // Simple login (not secure, just for demo)
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userService.validatePassword(user, password)) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(user);
    }

    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return ResponseEntity.ok(user);
    }
}