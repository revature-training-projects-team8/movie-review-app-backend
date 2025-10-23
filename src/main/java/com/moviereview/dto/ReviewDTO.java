package com.moviereview.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Review entities.
 * 
 * Purpose:
 * - Provides a clean API contract between frontend and backend
 * - Includes denormalized data (movieTitle, username) for efficient display
 * - Separates API representation from internal database structure
 * - Enables validation of incoming review data
 * 
 * Security Benefits:
 * - Prevents exposure of internal entity relationships
 * - Allows controlled data exposure (e.g., excluding sensitive user data)
 * - Protects against over-posting attacks
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    /**
     * Review identifier - null for new reviews, populated for existing ones
     */
    private Long id;
    
    /**
     * ID of the movie being reviewed
     */
    private Long movieId;
    
    /**
     * Movie title for display purposes (denormalized for efficiency)
     * Avoids need for frontend to make separate API call to get movie details
     */
    private String movieTitle;
    
    /**
     * ID of the user who wrote the review
     */
    private Long userId;
    
    /**
     * Username for display purposes (denormalized for efficiency)
     * Allows showing reviewer name without exposing sensitive user data
     */
    private String username;
    
    /**
     * Star rating from 1 to 5
     * Validated to ensure data integrity
     */
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    /**
     * Optional text comment with detailed review
     * Limited to 2000 characters for reasonable display and storage
     */
    @Size(max = 2000, message = "Comment must be less than 2000 characters")
    private String comment;
    
    /**
     * When the review was created
     * Used for sorting and displaying review chronology
     */
    private LocalDateTime reviewDate;

}