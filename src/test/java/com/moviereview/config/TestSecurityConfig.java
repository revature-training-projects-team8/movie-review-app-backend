package com.moviereview.config;

import com.moviereview.security.CustomUserDetailsService;
import com.moviereview.security.JwtUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test Security Configuration
 * Disables Spring Security for all tests and provides mock JWT components
 * This configuration takes precedence over the production SecurityConfig
 */
@TestConfiguration
public class TestSecurityConfig {

    /**
     * Mock JwtUtil for tests
     */
    @MockBean
    private JwtUtil jwtUtil;

    /**
     * Mock AuthenticationManager for tests
     */
    @MockBean
    private AuthenticationManager authenticationManager;

    /**
     * Mock CustomUserDetailsService for tests
     */
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Override the production security filter chain with a permissive one for tests
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /**
     * Password encoder for tests (same BCrypt as production)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
