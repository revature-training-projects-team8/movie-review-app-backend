package com.moviereview.repository;

import com.moviereview.model.Movie;
import com.moviereview.model.Review;
import com.moviereview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
}