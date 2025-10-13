package com.moviereview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
           @Bean
            public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
               http
                       .authorizeHttpRequests(auth -> auth
                                       .requestMatchers("/**").permitAll()
                                       .anyRequest().authenticated()
                       )
                       .csrf(csrf -> csrf.disable());
                           http.headers().frameOptions().disable();
 // Disable CSRF protection
                return http.build();
            }
}
