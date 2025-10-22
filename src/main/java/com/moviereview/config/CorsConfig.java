package com.moviereview.config;

import org.springframework.context.annotation.Configuration;

/**
 * CORS Configuration for Movie Review Application
 * 
 * NOTE: CORS is now configured in SecurityConfig.java as part of Spring
 * Security.
 * This file is kept for reference but is not actively used.
 * 
 * The SecurityConfig provides integrated CORS configuration with security
 * filters,
 * allowing React frontend (localhost:3001) to communicate with Spring Boot
 * backend (localhost:8080)
 */
@Configuration
public class CorsConfig {
    // CORS configuration moved to SecurityConfig.java for better integration with
    // Spring Security
}