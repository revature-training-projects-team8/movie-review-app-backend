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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Review Service Tests")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Movie testMovie;
    private User testUser;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testReview = new Review();
        testReview.setId(1L);
        testReview.setMovie(testMovie);
        testReview.setUser(testUser);
        testReview.setRating(5);
        testReview.setComment("Great movie!");
    }

    @Test
    @DisplayName("Should get reviews for a movie")
    void getReviewsForMovie_ShouldReturnReviews() {
        // Given
        Long movieId = 1L;
        List<Review> expectedReviews = Arrays.asList(testReview);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(testMovie));
        when(reviewRepository.findByMovie(testMovie)).thenReturn(expectedReviews);

        // When
        List<Review> actualReviews = reviewService.getReviewsForMovie(movieId);

        // Then
        assertThat(actualReviews).hasSize(1);
        assertThat(actualReviews.get(0).getRating()).isEqualTo(5);
        verify(movieRepository).findById(movieId);
        verify(reviewRepository).findByMovie(testMovie);
    }

    @Test
    @DisplayName("Should return empty list when movie not found")
    void getReviewsForMovie_WhenMovieNotFound_ShouldReturnEmptyList() {
        // Given
        Long movieId = 999L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        // When
        List<Review> actualReviews = reviewService.getReviewsForMovie(movieId);

        // Then
        assertThat(actualReviews).isEmpty();
        verify(movieRepository).findById(movieId);
        verify(reviewRepository, never()).findByMovie(any());
    }

    @Test
    @DisplayName("Should submit a new review")
    void submitReview_ShouldCreateReview() {
        // Given
        Long movieId = 1L;
        Long userId = 1L;
        Integer rating = 4;
        String comment = "Good movie";

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(testMovie));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(reviewRepository.findByMovieAndUser(testMovie, testUser)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        Review result = reviewService.submitReview(movieId, userId, rating, comment);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(5); // From testReview
        verify(movieRepository).findById(movieId);
        verify(userRepository).findById(userId);
        verify(reviewRepository).findByMovieAndUser(testMovie, testUser);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw exception when movie not found for review")
    void submitReview_WhenMovieNotFound_ShouldThrowException() {
        // Given
        Long movieId = 999L;
        Long userId = 1L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.submitReview(movieId, userId, 5, "Comment"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie not found with ID: " + movieId);

        verify(movieRepository).findById(movieId);
        verify(userRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user not found for review")
    void submitReview_WhenUserNotFound_ShouldThrowException() {
        // Given
        Long movieId = 1L;
        Long userId = 999L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(testMovie));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.submitReview(movieId, userId, 5, "Comment"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with ID: " + userId);

        verify(movieRepository).findById(movieId);
        verify(userRepository).findById(userId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user already reviewed movie")
    void submitReview_WhenDuplicateReview_ShouldThrowException() {
        // Given
        Long movieId = 1L;
        Long userId = 1L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(testMovie));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(reviewRepository.findByMovieAndUser(testMovie, testUser)).thenReturn(Optional.of(testReview));

        // When & Then
        assertThatThrownBy(() -> reviewService.submitReview(movieId, userId, 5, "Comment"))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("User has already reviewed this movie.");

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update existing review")
    void updateReview_ShouldUpdateReview() {
        // Given
        Long reviewId = 1L;
        Long userId = 1L;
        Integer newRating = 3;
        String newComment = "Updated comment";

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        Review result = reviewService.updateReview(reviewId, userId, newRating, newComment);

        // Then
        assertThat(result).isNotNull();
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).save(testReview);
        assertThat(testReview.getRating()).isEqualTo(newRating);
        assertThat(testReview.getComment()).isEqualTo(newComment);
    }

    @Test
    @DisplayName("Should throw exception when review not found for update")
    void updateReview_WhenReviewNotFound_ShouldThrowException() {
        // Given
        Long reviewId = 999L;
        Long userId = 1L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, userId, 4, "Comment"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Review not found with ID: " + reviewId);

        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user not authorized to update review")
    void updateReview_WhenNotAuthorized_ShouldThrowException() {
        // Given
        Long reviewId = 1L;
        Long wrongUserId = 999L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // When & Then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, wrongUserId, 4, "Comment"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("User not authorized to update this review.");

        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete existing review")
    void deleteReview_ShouldDeleteReview() {
        // Given
        Long reviewId = 1L;
        Long userId = 1L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // When
        reviewService.deleteReview(reviewId, userId);

        // Then
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).delete(testReview);
    }

    @Test
    @DisplayName("Should throw exception when user not authorized to delete review")
    void deleteReview_WhenNotAuthorized_ShouldThrowException() {
        // Given
        Long reviewId = 1L;
        Long wrongUserId = 999L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // When & Then
        assertThatThrownBy(() -> reviewService.deleteReview(reviewId, wrongUserId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("User not authorized to delete this review.");

        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository, never()).delete(any());
    }
}