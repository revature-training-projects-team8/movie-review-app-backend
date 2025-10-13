package com.moviereview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviereview.dto.ReviewDTO;
import com.moviereview.exception.DuplicateResourceException;
import com.moviereview.exception.ValidationException;
import com.moviereview.model.Movie;
import com.moviereview.model.Review;
import com.moviereview.model.User;
import com.moviereview.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@DisplayName("Review Controller Tests")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private Review testReview;
    private List<Review> testReviews;
    private Movie testMovie;
    private User testUser;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("The Matrix");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testReview = new Review();
        testReview.setId(1L);
        testReview.setMovie(testMovie);
        testReview.setUser(testUser);
        testReview.setRating(5);
        testReview.setComment("Amazing movie!");
        testReview.setReviewDate(LocalDateTime.now());

        Review testReview2 = new Review();
        testReview2.setId(2L);
        testReview2.setMovie(testMovie);
        testReview2.setUser(testUser);
        testReview2.setRating(4);
        testReview2.setComment("Good movie!");
        testReview2.setReviewDate(LocalDateTime.now());

        testReviews = Arrays.asList(testReview, testReview2);
    }

    @Test
    @DisplayName("A.2 - Should get reviews for a specific movie")
    void getReviewsByMovie_ShouldReturnReviews() throws Exception {
        // Given
        Long movieId = 1L;
        when(reviewService.getReviewsForMovie(movieId)).thenReturn(testReviews);

        // When & Then
        mockMvc.perform(get("/api/reviews/movie/" + movieId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("Amazing movie!"))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].rating").value(4));

        verify(reviewService).getReviewsForMovie(movieId);
    }

    @Test
    @DisplayName("B.3 - Should get reviews by a specific user")
    void getReviewsByUser_ShouldReturnUserReviews() throws Exception {
        // Given
        Long userId = 1L;
        when(reviewService.getReviewsByUser(userId)).thenReturn(testReviews);

        // When & Then
        mockMvc.perform(get("/api/reviews/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].movieTitle").value("The Matrix"));

        verify(reviewService).getReviewsByUser(userId);
    }

    @Test
    @DisplayName("B.2 - Should submit a review for a movie")
    void submitReview_ShouldCreateReview() throws Exception {
        // Given
        Long movieId = 1L;
        Long userId = 1L;
        ReviewDTO reviewDto = new ReviewDTO();
        reviewDto.setRating(5);
        reviewDto.setComment("Great movie!");

        when(reviewService.submitReview(movieId, userId, 5, "Great movie!")).thenReturn(testReview);

        // When & Then
        mockMvc.perform(post("/api/reviews/movie/" + movieId + "/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Amazing movie!"))
                .andExpect(jsonPath("$.movieTitle").value("The Matrix"));

        verify(reviewService).submitReview(movieId, userId, 5, "Great movie!");
    }

    @Test
    @DisplayName("B.2 - Should validate review data when submitting")
    void submitReview_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given - Invalid rating (outside 1-5 range)
        Long movieId = 1L;
        Long userId = 1L;
        ReviewDTO invalidDto = new ReviewDTO();
        invalidDto.setRating(6); // Invalid - outside range
        invalidDto.setComment("Valid comment");

        // When & Then
        mockMvc.perform(post("/api/reviews/movie/" + movieId + "/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).submitReview(anyLong(), anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("B.3 - Should update own review")
    void updateReview_ShouldUpdateExistingReview() throws Exception {
        // Given
        Long reviewId = 1L;
        Long userId = 1L;
        ReviewDTO updateDto = new ReviewDTO();
        updateDto.setRating(4);
        updateDto.setComment("Updated comment");

        Review updatedReview = new Review();
        updatedReview.setId(reviewId);
        updatedReview.setMovie(testMovie);
        updatedReview.setUser(testUser);
        updatedReview.setRating(4);
        updatedReview.setComment("Updated comment");
        updatedReview.setReviewDate(LocalDateTime.now());

        when(reviewService.updateReview(reviewId, userId, 4, "Updated comment")).thenReturn(updatedReview);

        // When & Then
        mockMvc.perform(put("/api/reviews/" + reviewId + "/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Updated comment"));

        verify(reviewService).updateReview(reviewId, userId, 4, "Updated comment");
    }

    @Test
    @DisplayName("B.3 - Should prevent updating other user's review")
    void updateReview_WhenNotAuthorized_ShouldReturnBadRequest() throws Exception {
        // Given
        Long reviewId = 1L;
        Long wrongUserId = 999L; // Different user trying to update
        ReviewDTO updateDto = new ReviewDTO();
        updateDto.setRating(4);
        updateDto.setComment("Unauthorized update");

        when(reviewService.updateReview(reviewId, wrongUserId, 4, "Unauthorized update"))
                .thenThrow(new ValidationException("User not authorized to update this review."));

        // When & Then
        mockMvc.perform(put("/api/reviews/" + reviewId + "/user/" + wrongUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());

        verify(reviewService).updateReview(reviewId, wrongUserId, 4, "Unauthorized update");
    }

    @Test
    @DisplayName("B.3 - Should delete own review")
    void deleteReview_ShouldRemoveReview() throws Exception {
        // Given
        Long reviewId = 1L;
        Long userId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/reviews/" + reviewId + "/user/" + userId))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(reviewId, userId);
    }

    @Test
    @DisplayName("B.3 - Should prevent deleting other user's review")
    void deleteReview_WhenNotAuthorized_ShouldReturnBadRequest() throws Exception {
        // Given
        Long reviewId = 1L;
        Long wrongUserId = 999L;

        doThrow(new ValidationException("User not authorized to delete this review."))
                .when(reviewService).deleteReview(reviewId, wrongUserId);

        // When & Then
        mockMvc.perform(delete("/api/reviews/" + reviewId + "/user/" + wrongUserId))
                .andExpect(status().isBadRequest());

        verify(reviewService).deleteReview(reviewId, wrongUserId);
    }

    @Test
    @DisplayName("Should prevent duplicate reviews for same movie by same user")
    void submitReview_WhenDuplicateReview_ShouldReturnConflict() throws Exception {
        // Given
        Long movieId = 1L;
        Long userId = 1L;
        ReviewDTO reviewDto = new ReviewDTO();
        reviewDto.setRating(5);
        reviewDto.setComment("Duplicate review");

        when(reviewService.submitReview(movieId, userId, 5, "Duplicate review"))
                .thenThrow(new DuplicateResourceException("User has already reviewed this movie."));

        // When & Then
        mockMvc.perform(post("/api/reviews/movie/" + movieId + "/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isConflict());

        verify(reviewService).submitReview(movieId, userId, 5, "Duplicate review");
    }
}