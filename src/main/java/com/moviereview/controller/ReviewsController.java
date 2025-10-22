package com.moviereview.controller;

import com.moviereview.dto.ReviewDTO;
import com.moviereview.model.Review;
import com.moviereview.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReviewsController - Handles review endpoints WITHOUT /api prefix
 * This controller provides the same functionality as ReviewController
 * but at /reviews/** instead of /api/reviews/**
 */
@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" })
public class ReviewsController {

    private final ReviewService reviewService;

    public ReviewsController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Get reviews for a specific movie
    @GetMapping("/movie/{movieId}")
    public List<ReviewDTO> getReviewsByMovie(@PathVariable Long movieId) {
        return reviewService.getReviewsForMovie(movieId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get reviews by a specific user
    @GetMapping("/user/{userId}")
    public List<ReviewDTO> getReviewsByUser(@PathVariable Long userId) {
        return reviewService.getReviewsByUser(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Submit a review (simplified - no authentication required)
    @PostMapping("/movie/{movieId}/user/{userId}")
    public ResponseEntity<ReviewDTO> submitReview(
            @PathVariable Long movieId,
            @PathVariable Long userId,
            @Valid @RequestBody ReviewDTO reviewDto) {
        Review savedReview = reviewService.submitReview(movieId, userId, reviewDto.getRating(), reviewDto.getComment());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedReview));
    }

    // Edit a review
    @PutMapping("/{reviewId}/user/{userId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @PathVariable Long userId,
            @Valid @RequestBody ReviewDTO reviewDto) {
        Review updatedReview = reviewService.updateReview(reviewId, userId, reviewDto.getRating(),
                reviewDto.getComment());
        return ResponseEntity.ok(convertToDto(updatedReview));
    }

    // Delete a review
    @DeleteMapping("/{reviewId}/user/{userId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewService.deleteReview(reviewId, userId);
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
