package com.moviereview.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;

/**
 * Configuration class that loads environment variables from .env file.
 * This approach uses a static block to load .env variables before Spring starts,
 * eliminating the need for META-INF/spring.factories.
 * 
 * The static block runs when the class is first loaded, which happens early
 * enough in the JVM lifecycle to make the variables available to Spring Boot.
 */
@Configuration
public class EnvironmentConfig {
    
    // Static block runs when the class is first loaded by the JVM
    // This happens early enough to set system properties before Spring Boot
    // tries to resolve ${DB_URL}, ${DB_USERNAME}, and ${DB_PASSWORD}
    static {
        try {
            // Load .env file from the project root
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMissing()
                    .load();
            
            // Set system properties so Spring can access them
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
            
            System.out.println("✓ Environment variables loaded from .env file");
            
        } catch (Exception e) {
            System.out.println("⚠ Warning: Could not load .env file - " + e.getMessage());
            System.out.println("Application will use default or system environment variables");
        }
    }
    
    @Autowired
    private Environment environment;
    
    @PostConstruct
    public void verifyEnvironmentLoading() {
        // Optional: Verify that environment variables were loaded correctly
        String dbUrl = environment.getProperty("DB_URL");
        if (dbUrl != null && !dbUrl.startsWith("${")) {
            System.out.println("✓ Database configuration loaded successfully");
        }
    }
}