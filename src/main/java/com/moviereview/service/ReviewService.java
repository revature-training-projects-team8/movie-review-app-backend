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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, MovieRepository movieRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public List<Review> getReviewsForMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .map(reviewRepository::findByMovie)
                .orElse(List.of()); // Return empty list if movie not found
    }

    public List<Review> getReviewsByUser(Long userId) {
        return userRepository.findById(userId)
                .map(reviewRepository::findByUser)
                .orElse(List.of());
    }

    public Review submitReview(Long movieId, Long userId, Integer rating, String comment) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Optional: Prevent multiple reviews per user per movie
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

    public void deleteReview(Long reviewId, Long userId) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        if (!existingReview.getUser().getId().equals(userId)) {
            throw new ValidationException("User not authorized to delete this review.");
        }

        reviewRepository.delete(existingReview);
    }
}