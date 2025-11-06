package com.moviereview.config;

import com.moviereview.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security Configuration for Movie Review Application with JWT
 * Configures Spring Security with JWT authentication, CORS support, and BCrypt password encoding
 * Only active in non-test profiles
 */
@Configuration
@EnableWebSecurity
@Profile("!test")
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with custom configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable CSRF for REST API (JWT doesn't need CSRF protection)
                .csrf(csrf -> csrf.disable())

                // Set session management to stateless (JWT-based authentication)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(
                                "/auth/**", // Auth endpoints
                                "/error", // Error page
                                "/actuator/health" // Health check endpoint
                        ).permitAll()
                        
                        // Public read-only movie endpoints
                        .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                        
                        // Admin-only movie management operations
                        .requestMatchers(HttpMethod.POST, "/api/movies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/movies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/movies/**").hasRole("ADMIN")
                        
                        // Public read-only review endpoints (movie reviews, recent reviews, and all reviews)
                        .requestMatchers(HttpMethod.GET, "/api/reviews/movie/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/recent").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/all").permitAll()
                        
                        // Protected personal review endpoints - require authentication
                        .requestMatchers(HttpMethod.GET, "/api/reviews/my-reviews").authenticated()
                        
                        // Protected review write operations - require authentication
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated())

                // Add JWT authentication filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Authentication Manager Bean
     * Required for authenticating users during login
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS Configuration Source
     * Defines which origins, methods, and headers are allowed
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow specific origins (exact matches)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",    // Vite default
                "http://localhost:5174",    // Vite alternative
                "http://trng2309-8.s3-website-us-east-1.amazonaws.com",  // S3 frontend
                "https://trng2309-8.s3-website-us-east-1.amazonaws.com"  // S3 frontend HTTPS
        ));

        // Allow pattern-based origins for localhost flexibility
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://127.0.0.1:*",       // Any localhost IP
                "http://localhost:*",        // Any localhost port
                "http://*.s3-website-us-east-1.amazonaws.com",           // S3 pattern
                "https://*.s3-website-us-east-1.amazonaws.com"           // S3 HTTPS pattern
        ));

        // Allow all standard HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));

        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Expose these headers to the frontend
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"));

        // Cache preflight requests for 1 hour
        configuration.setMaxAge(3600L);

        // Apply CORS configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Password Encoder Bean
     * Uses BCrypt for password hashing (matches your database admin password
     * format)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
