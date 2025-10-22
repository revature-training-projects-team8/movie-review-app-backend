package com.moviereview.service;

import com.moviereview.model.Movie;
import com.moviereview.repository.MovieRepository;
import com.moviereview.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository; // To calculate average rating

    public MovieService(MovieRepository movieRepository, ReviewRepository reviewRepository) {
        this.movieRepository = movieRepository;
        this.reviewRepository = reviewRepository;
    }

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

    // Delete a movie
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}
