package com.moviereview.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Review entity representing user reviews and ratings for movies.
 * 
 * Business Rules:
 * - Each user can only review each movie once (enforced at service layer)
 * - Users can edit/delete their own reviews
 * - ADMIN users can delete any review for moderation purposes
 * - Reviews contribute to the movie's average rating calculation
 * 
 * Security:
 * - User identity is extracted from JWT token, not URL parameters
 * - Users cannot manipulate other users' reviews via direct ID access
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {
    
    /**
     * Primary key - auto-generated unique identifier for each review
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Many-to-one relationship with Movie entity.
     * Each review belongs to exactly one movie.
     * LAZY fetching for performance - movie details loaded only when accessed.
     * JsonIgnoreProperties prevents circular reference during serialization.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonIgnoreProperties({"reviews"})
    private Movie movie;
    
    /**
     * Many-to-one relationship with User entity.
     * Each review is written by exactly one user.
     * LAZY fetching for performance - user details loaded only when accessed.
     * JsonIgnoreProperties prevents circular reference during serialization.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"reviews"})
    private User user;
    
    /**
     * Star rating from 1 to 5.
     * Required field used for calculating movie's average rating.
     * Validation ensures rating is within valid range.
     */
    @Column(nullable = false)
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    /**
     * Optional text comment with the user's detailed thoughts about the movie.
     * Stored as TEXT to allow longer comments up to 2000 characters.
     * Can be null if user only wants to provide a star rating.
     */
    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "Comment must be less than 2000 characters")
    private String comment;
    
    /**
     * Timestamp when the review was created.
     * Automatically set when the review is first persisted.
     * Used for sorting reviews (newest first) and audit purposes.
     */
    private LocalDateTime reviewDate;

    /**
     * JPA lifecycle callback - automatically executed before persisting a new review.
     * Sets the review creation timestamp to current date/time.
     */
    @PrePersist
    protected void onCreate() {
        reviewDate = LocalDateTime.now();
    }

}