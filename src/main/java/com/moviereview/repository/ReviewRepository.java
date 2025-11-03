package com.moviereview.repository;

import com.moviereview.model.Movie;
import com.moviereview.model.Review;
import com.moviereview.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Review entity operations.
 * 
 * Extends JpaRepository to provide standard CRUD operations.
 * Includes custom queries with JOIN FETCH to prevent LazyInitializationException
 * when accessing related Movie and User entities outside of transaction scope.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Finds all reviews for a specific movie with eager loading.
     * Uses JOIN FETCH to load associated movie and user data in a single query,
     * preventing N+1 query problems and LazyInitializationException.
     * 
     * @param movie The movie entity to find reviews for
     * @return List of reviews with movie and user data eagerly loaded
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.movie JOIN FETCH r.user WHERE r.movie = :movie")
    List<Review> findByMovie(@Param("movie") Movie movie);

    /**
     * Finds all reviews written by a specific user with eager loading.
     * Uses JOIN FETCH to load associated movie and user data in a single query,
     * preventing N+1 query problems and LazyInitializationException.
     * 
     * @param user The user entity to find reviews for
     * @return List of reviews with movie and user data eagerly loaded
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.movie JOIN FETCH r.user WHERE r.user = :user")
    List<Review> findByUser(@Param("user") User user);

    /**
     * Finds a review by movie and user combination.
     * Used to enforce business rule: each user can only review each movie once.
     * 
     * @param movie The movie entity
     * @param user The user entity
     * @return Optional containing the review if exists, empty otherwise
     */
    Optional<Review> findByMovieAndUser(Movie movie, User user);

    /**
     * Deletes all reviews for a specific movie by movie ID.
     * Used when deleting a movie to handle foreign key constraints.
     * 
     * @param movieId The ID of the movie whose reviews should be deleted
     */
    @Modifying
    @Query("DELETE FROM Review r WHERE r.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);

    /**
     * Finds the most recent reviews ordered by creation date.
     * Uses JOIN FETCH to load associated movie and user data in a single query.
     * 
     * @param pageable Pageable object to limit the number of results
     * @return List of the most recent reviews with movie and user data eagerly loaded
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.movie JOIN FETCH r.user ORDER BY r.reviewDate DESC")
    List<Review> findTopRecentReviews(Pageable pageable);

    /**
     * Finds all reviews with eager loading of movie and user data.
     * Uses JOIN FETCH to load associated movie and user data in a single query,
     * preventing N+1 query problems and LazyInitializationException.
     * 
     * @return List of all reviews with movie and user data eagerly loaded
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.movie JOIN FETCH r.user ORDER BY r.reviewDate DESC")
    List<Review> findAllReviewsWithMovieAndUser();

    /**
     * Finds all reviews for a specific movie by movie ID.
     * Used for calculating average ratings - doesn't need JOIN FETCH since we only need ratings.
     * 
     * @param movieId The ID of the movie
     * @return List of reviews for the movie ordered by review date (newest first)
     */
    @Query("SELECT r FROM Review r WHERE r.movie.id = :movieId ORDER BY r.reviewDate DESC")
    List<Review> findByMovieIdOrderByReviewDateDesc(@Param("movieId") Long movieId);

    /**
     * Gets the movie ID for a specific review without loading the Movie entity.
     * This prevents LazyInitializationException when accessing movie data outside transaction scope.
     * 
     * @param reviewId The ID of the review
     * @return The movie ID associated with the review, or null if review not found
     */
    @Query("SELECT r.movie.id FROM Review r WHERE r.id = :reviewId")
    Long getMovieIdByReviewId(@Param("reviewId") Long reviewId);

    /**
     * Finds a review by ID with eager loading of movie and user data.
     * This prevents LazyInitializationException when accessing related entities outside transaction scope.
     * 
     * @param reviewId The ID of the review to find
     * @return Optional of review with movie and user data eagerly loaded
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.movie JOIN FETCH r.user WHERE r.id = :reviewId")
    Optional<Review> findByIdWithMovieAndUser(@Param("reviewId") Long reviewId);
}