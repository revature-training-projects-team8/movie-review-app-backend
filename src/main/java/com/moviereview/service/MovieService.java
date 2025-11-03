package com.moviereview.service;

import com.moviereview.exception.ResourceNotFoundException;
import com.moviereview.model.Movie;
import com.moviereview.repository.MovieRepository;
import com.moviereview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository; // To calculate average rating

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    public List<Movie> searchMovies(String searchTerm) {
        return movieRepository.searchMovies(searchTerm);
    }

    public double getAverageRatingForMovie(Long movieId) {
        Optional<Movie> movieOptional = movieRepository.findById(movieId);
        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            return reviewRepository.findByMovie(movie).stream()
                    .mapToInt(review -> review.getRating())
                    .average()
                    .orElse(0.0);
        }
        return 0.0;
    }

    // Admin-like functionality to add/update movies
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

        /**
     * Deletes a movie by its ID.
     * 
     * The @Transactional annotation ensures that the deletion happens in a single transaction.
     * We explicitly delete associated reviews first to handle foreign key constraints.
     * 
     * @param id The ID of the movie to delete
     * @throws ResourceNotFoundException if movie with given ID is not found
     */
    @Transactional
    public void deleteMovie(Long id) {
        // First, verify the movie exists
        movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        
        // Explicitly delete all reviews for this movie first
        // This handles the foreign key constraint at the database level
        reviewRepository.deleteByMovieId(id);
        
        // Now delete the movie
        movieRepository.deleteById(id);
    }

    /**
     * Updates the average rating for a movie based on all its reviews.
     * Calculates the average from all reviews and updates the movie entity.
     * 
     * @param movieId The ID of the movie to update the rating for
     */
    @Transactional
    public void updateMovieAverageRating(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        
        // Get all reviews for this movie
        List<com.moviereview.model.Review> reviews = reviewRepository.findByMovie(movie);
        
        if (reviews.isEmpty()) {
            // No reviews, set rating to 0
            movie.setAvgRating(0.0);
        } else {
            // Calculate average rating
            double averageRating = reviews.stream()
                .mapToInt(com.moviereview.model.Review::getRating)
                .average()
                .orElse(0.0);
            
            // Round to 2 decimal places
            movie.setAvgRating(Math.round(averageRating * 100.0) / 100.0);
        }
        
        movieRepository.save(movie);
    }

    /**
     * Recalculates and updates average ratings for all movies in the database.
     * Useful for data migration or fixing rating inconsistencies.
     * This method should be used carefully as it processes all movies.
     */
    @Transactional
    public void recalculateAllMovieRatings() {
        List<Movie> allMovies = movieRepository.findAll();
        
        for (Movie movie : allMovies) {
            updateMovieAverageRating(movie.getId());
        }
    }
}
