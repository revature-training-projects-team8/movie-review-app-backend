package com.moviereview.service;

import com.moviereview.model.Movie;
import com.moviereview.repository.MovieRepository;
import com.moviereview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ReviewRepository reviewRepository; // To calculate average rating

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
        List<Movie> movies = movieRepository.findAll();
        for(Movie m : movies){
            if(m.getId().equals(id)){
                reviewRepository.deleteAll(reviewRepository.findByMovie(m));
            }
       }
       movieRepository.deleteById(id);
    }
}
