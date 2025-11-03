package com.moviereview.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

/**
 * Movie entity representing films in the movie review system.
 * 
 * Only ADMIN users can create, update, or delete movies.
 * All users (including anonymous) can view movies and their details.
 * 
 * The avgRating field is calculated dynamically based on user reviews
 * and is updated whenever reviews are added, modified, or deleted.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movies")
public class Movie {
    
    /**
     * Primary key - auto-generated unique identifier for each movie
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Movie title - required field, used for display and search functionality.
     * Maximum length of 255 characters to fit in standard VARCHAR column.
     */
    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;
    
    /**
     * Detailed description or plot summary of the movie.
     * Stored as TEXT to allow longer descriptions up to 5000 characters.
     */
    @Column(columnDefinition = "TEXT")
    @Size(max = 5000, message = "Description must be less than 5000 characters")
    private String description;
    
    /**
     * The date when the movie was officially released.
     * Used for sorting, filtering, and display purposes.
     */
    private LocalDate releaseDate;
    
    /**
     * Name of the movie director.
     * Used for search functionality and movie information display.
     */
    @Size(max = 255, message = "Director name must be less than 255 characters")
    private String director;
    
    /**
     * Movie genre (e.g., "Drama", "Action", "Comedy").
     * Used for categorization and search filtering.
     */
    @Size(max = 100, message = "Genre must be less than 100 characters")
    private String genre;

    /**
     * URL pointing to the movie poster image.
     * Can be a relative path or full URL to an image hosting service.
     */
    private String posterUrl;

    /**
     * Movie runtime in minutes.
     * Optional field - can be null if duration is not available.
     */
    private Integer duration;

    /**
     * Calculated average rating from all user reviews.
     * Range: 0.0 to 5.0 (assuming 5-star rating system)
     * Automatically updated when reviews are added/modified/deleted.
     */
    @Column(columnDefinition = "DECIMAL(3,2) DEFAULT 0.0")
    private Double avgRating = 0.0;

    /**
     * One-to-many relationship with reviews for this movie.
     * When a movie is deleted, all associated reviews are also deleted.
     * JsonIgnore prevents infinite recursion during JSON serialization.
     */
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Review> reviews;

  
}
