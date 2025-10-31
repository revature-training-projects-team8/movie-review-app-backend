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
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        
        // Explicitly delete all reviews for this movie first
        // This handles the foreign key constraint at the database level
        reviewRepository.deleteByMovieId(id);
        
        // Now delete the movie
        movieRepository.deleteById(id);
    }
}
