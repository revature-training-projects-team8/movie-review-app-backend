package com.moviereview.controller;

import com.moviereview.dto.ReviewDTO;
import com.moviereview.model.Review;
import com.moviereview.model.User;
import com.moviereview.service.ReviewService;
import com.moviereview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for managing movie reviews.
 * 
 * Endpoints:
 * - GET /api/reviews/movie/{movieId} - Public: Get all reviews for a movie
 * - GET /api/reviews/recent?limit=10 - Public: Get most recent reviews (default: 10, max: 50)
 * - GET /api/reviews/my-reviews - Authenticated: Get current user's reviews
 * - POST /api/reviews/movie/{movieId} - Authenticated: Submit a new review
 * - PUT /api/reviews/{reviewId} - Authenticated: Update own review
 * - DELETE /api/reviews/{reviewId} - Authenticated: Delete own review (or any review if ADMIN)
 * 
 * Security Features:
 * - User identity extracted from JWT token, not URL parameters
 * - Users can only modify their own reviews (unless ADMIN)
 * - All write operations require authentication
 * - ADMIN users can delete any review for content moderation
 */
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" }, allowCredentials = "true")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    /**
     * Retrieves all reviews for a specific movie.
     * Public endpoint - no authentication required.
     * 
     * @param movieId The ID of the movie to get reviews for
     * @return List of ReviewDTO objects containing review details
     */
    @GetMapping("/movie/{movieId}")
    public List<ReviewDTO> getReviewsByMovie(@PathVariable Long movieId) {
        return reviewService.getReviewsForMovie(movieId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all reviews written by the currently authenticated user.
     * Secure endpoint - user identity extracted from JWT token.
     * 
     * @return List of ReviewDTO objects for the authenticated user's reviews
     */
    @GetMapping("/my-reviews")
    public List<ReviewDTO> getMyReviews() {
        // Extract authenticated user from JWT token in SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Look up user by username from JWT token
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return List.of(); // Return empty list if user not found (shouldn't happen with valid JWT)
        }
        
        User user = userOptional.get();
        return reviewService.getReviewsByUser(user.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the most recent reviews in the system.
     * Public endpoint - no authentication required.
     * Returns up to 10 of the newest reviews by default.
     * 
     * @param limit Optional parameter to specify number of reviews (default: 10, max: 50)
     * @return List of ReviewDTO objects for the most recent reviews
     */
    @GetMapping("/recent")
    public List<ReviewDTO> getRecentReviews(@RequestParam(defaultValue = "10") int limit) {
        // Limit the maximum number of reviews to prevent performance issues
        int safeLimit = Math.min(limit, 50);
        
        return reviewService.getRecentReviews(safeLimit).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Submit a review - User ID is extracted from JWT token
    @PostMapping("/movie/{movieId}")
    public ResponseEntity<ReviewDTO> submitReview(
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewDTO reviewDto) {
        
        // Get authenticated user from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Find user by username
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        User user = userOptional.get();
        Review savedReview = reviewService.submitReview(movieId, user.getId(), reviewDto.getRating(), reviewDto.getComment());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedReview));
    }

    // Edit a review - User ID is extracted from JWT token
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDTO reviewDto) {
        
        // Get authenticated user from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Find user by username
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        User user = userOptional.get();
        Review updatedReview = reviewService.updateReview(reviewId, user.getId(), reviewDto.getRating(),
                reviewDto.getComment());
        return ResponseEntity.ok(convertToDto(updatedReview));
    }

    // Delete a review - User ID is extracted from JWT token, ADMIN can delete any review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        // Get authenticated user from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Find user by username
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        User user = userOptional.get();
        
        // Check if user has ADMIN role
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        // Use overloaded method that supports admin privileges
        reviewService.deleteReview(reviewId, user.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    private ReviewDTO convertToDto(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setMovieId(review.getMovie().getId());
        dto.setMovieTitle(review.getMovie().getTitle());
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setReviewDate(review.getReviewDate());
        return dto;
    }
}