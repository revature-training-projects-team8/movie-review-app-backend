package com.moviereview.service;

import com.moviereview.exception.DuplicateResourceException;
import com.moviereview.exception.ResourceNotFoundException;
import com.moviereview.exception.ValidationException;
import com.moviereview.model.Movie;
import com.moviereview.model.Review;
import com.moviereview.model.User;
import com.moviereview.repository.MovieRepository;
import com.moviereview.repository.ReviewRepository;
import com.moviereview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for managing movie reviews.
 * 
 * Handles business logic for:
 * - Retrieving reviews for movies and users
 * - Creating new reviews with validation
 * - Updating existing reviews (owner or admin only)
 * - Deleting reviews (owner or admin only)
 * 
 * Security: All user identity verification is handled at the controller layer
 * using JWT tokens, ensuring users can only modify their own reviews
 * (unless they are ADMIN users).
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves all reviews for a specific movie.
     * 
     * @param movieId The ID of the movie to get reviews for
     * @return List of reviews for the movie, empty list if movie doesn't exist
     */
    public List<Review> getReviewsForMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .map(reviewRepository::findByMovie)
                .orElse(List.of()); // Return empty list if movie not found
    }

    /**
     * Retrieves all reviews written by a specific user.
     * Used for "My Reviews" functionality.
     * 
     * @param userId The ID of the user to get reviews for
     * @return List of reviews by the user, empty list if user doesn't exist
     */
    public List<Review> getReviewsByUser(Long userId) {
        return userRepository.findById(userId)
                .map(reviewRepository::findByUser)
                .orElse(List.of());
    }

    /**
     * Creates a new review for a movie.
     * 
     * Business Rules:
     * - Each user can only review each movie once
     * - Both movie and user must exist
     * - Rating must be between 1-5 (validated at entity level)
     * 
     * @param movieId The ID of the movie being reviewed
     * @param userId The ID of the user writing the review
     * @param rating The star rating (1-5)
     * @param comment Optional text comment
     * @return The saved review entity
     * @throws ResourceNotFoundException if movie or user doesn't exist
     * @throws DuplicateResourceException if user has already reviewed this movie
     */
    public Review submitReview(Long movieId, Long userId, Integer rating, String comment) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Enforce business rule: one review per user per movie
        reviewRepository.findByMovieAndUser(movie, user).ifPresent(r -> {
            throw new DuplicateResourceException("User has already reviewed this movie.");
        });

        Review review = new Review();
        review.setMovie(movie);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    /**
     * Updates an existing review.
     * Only the review owner can update their review.
     * 
     * @param reviewId The ID of the review to update
     * @param userId The ID of the user attempting the update (for authorization)
     * @param rating The new star rating (1-5)
     * @param comment The new comment text
     * @return The updated review entity
     * @throws ResourceNotFoundException if review doesn't exist
     * @throws ValidationException if user is not the review owner
     */
    public Review updateReview(Long reviewId, Long userId, Integer rating, String comment) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        if (!existingReview.getUser().getId().equals(userId)) {
            throw new ValidationException("User not authorized to update this review.");
        }

        existingReview.setRating(rating);
        existingReview.setComment(comment);
        return reviewRepository.save(existingReview);
    }

    /**
     * Deletes a review.
     * Only the review owner can delete their review (regular users).
     * This method is for regular user deletions only.
     * 
     * @param reviewId The ID of the review to delete
     * @param userId The ID of the user attempting the deletion (for authorization)
     * @throws ResourceNotFoundException if review doesn't exist
     * @throws ValidationException if user is not the review owner
     */
    public void deleteReview(Long reviewId, Long userId) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        if (!existingReview.getUser().getId().equals(userId)) {
            throw new ValidationException("User not authorized to delete this review.");
        }

        reviewRepository.delete(existingReview);
    }

    /**
     * Deletes a review with admin privileges support.
     * Overloaded method that allows ADMIN users to delete any review for content moderation.
     * 
     * @param reviewId The ID of the review to delete
     * @param userId The ID of the user attempting the deletion
     * @param isAdmin Whether the user has ADMIN role (extracted from JWT token)
     * @throws ResourceNotFoundException if review doesn't exist
     * @throws ValidationException if regular user tries to delete someone else's review
     */
    public void deleteReview(Long reviewId, Long userId, boolean isAdmin) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        // ADMIN can delete any review for moderation, regular users can only delete their own
        if (!isAdmin && !existingReview.getUser().getId().equals(userId)) {
            throw new ValidationException("User not authorized to delete this review.");
        }

        reviewRepository.delete(existingReview);
    }

    /**
     * Retrieves the most recent reviews in the system.
     * Returns up to the specified limit of reviews ordered by creation date (newest first).
     * 
     * @param limit The maximum number of reviews to return (default: 10)
     * @return List of the most recent reviews
     */
    public List<Review> getRecentReviews(int limit) {
        return reviewRepository.findTopRecentReviews(
            org.springframework.data.domain.PageRequest.of(0, limit)
        );
    }
}