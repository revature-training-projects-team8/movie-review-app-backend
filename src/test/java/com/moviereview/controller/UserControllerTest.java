package com.moviereview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviereview.model.User;
import com.moviereview.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("User Controller Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
    }

    @Test
    @DisplayName("B.1 - Should register new user with unique username")
    void registerUser_WithValidData_ShouldCreateUser() throws Exception {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("password123");

        when(userService.createUser("newuser", "password123")).thenReturn(savedUser);

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("newuser"));

        verify(userService).createUser("newuser", "password123");
    }

    @Test
    @DisplayName("B.1 - Should reject duplicate username registration")
    void registerUser_WithDuplicateUsername_ShouldReturnConflict() throws Exception {
        // Given
        User duplicateUser = new User();
        duplicateUser.setUsername("existinguser");
        duplicateUser.setPassword("password123");

        when(userService.createUser("existinguser", "password123"))
                .thenThrow(new RuntimeException("Username already exists."));

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isConflict());

        verify(userService).createUser("existinguser", "password123");
    }

    @Test
    @DisplayName("B.1 - Should validate user registration data")
    void registerUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given - Username too short (violates @Size(min = 3))
        User invalidUser = new User();
        invalidUser.setUsername("ab"); // Too short
        invalidUser.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(anyString(), anyString());
    }

    @Test
    @DisplayName("B.1 - Should authenticate user login with valid credentials")
    void loginUser_WithValidCredentials_ShouldReturnUser() throws Exception {
        // Given
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "testuser");
        credentials.put("password", "password123");

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userService.validatePassword(testUser, "password123")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).findByUsername("testuser");
        verify(userService).validatePassword(testUser, "password123");
    }

    @Test
    @DisplayName("B.1 - Should reject login with invalid credentials")
    void loginUser_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Given
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "testuser");
        credentials.put("password", "wrongpassword");

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userService.validatePassword(testUser, "wrongpassword")).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());

        verify(userService).findByUsername("testuser");
        verify(userService).validatePassword(testUser, "wrongpassword");
    }

    @Test
    @DisplayName("B.1 - Should reject login with non-existent user")
    void loginUser_WithNonExistentUser_ShouldReturnNotFound() throws Exception {
        // Given
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "nonexistent");
        credentials.put("password", "password123");

        when(userService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isNotFound());

        verify(userService).findByUsername("nonexistent");
        verify(userService, never()).validatePassword(any(), anyString());
    }

    @Test
    @DisplayName("Should get user by ID")
    void getUserById_ShouldReturnUser() throws Exception {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should get user by username")
    void getUserByUsername_ShouldReturnUser() throws Exception {
        // Given
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).findByUsername("testuser");
    }
}