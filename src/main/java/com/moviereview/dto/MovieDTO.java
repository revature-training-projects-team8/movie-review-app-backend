package com.moviereview.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for Movie entities.
 * 
 * Purpose:
 * - Clean API contract for movie data exchange
 * - Excludes internal relationships (reviews collection) for performance
 * - Includes calculated averageRating field for display
 * - Provides validation for movie creation/updates
 * 
 * Usage:
 * - Public API responses for movie listings and details
 * - Input validation for ADMIN movie management operations
 * - Prevents exposure of internal entity structure
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieDTO {
    /**
     * Movie identifier - null for new movies, populated for existing ones
     */
    private Long id;
    
    /**
     * Movie title - required field for creation/updates
     * Used for display and search functionality
     */
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;
    
    /**
     * Detailed plot summary or description
     * Optional field, can be quite lengthy for comprehensive descriptions
     */
    @Size(max = 5000, message = "Description must be less than 5000 characters")
    private String description;
    
    /**
     * Official release date of the movie
     * Used for sorting, filtering, and chronological display
     */
    private LocalDate releaseDate;
    
    /**
     * Name of the movie director
     * Used for credits and search functionality
     */
    private String director;
    
    /**
     * Movie genre for categorization
     * Examples: "Drama", "Action", "Comedy", "Science Fiction"
     */
    private String genre;
    
    /**
     * URL to movie poster image
     * Can be relative path or full URL to image hosting service
     */
    private String posterUrl;
    
    /**
     * Movie runtime in minutes
     * Optional field for display purposes
     */
    private Integer duration;
    
    /**
     * Calculated average rating from all user reviews
     * Dynamically computed and included in API responses
     * Range: 0.0 to 5.0 based on 5-star rating system
     */
    private double averageRating;

    
}