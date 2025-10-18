package com.moviereview.service;

import com.moviereview.model.Movie;
import com.moviereview.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }

    public Optional<Movie> update(Long id, Movie movie) {
        return movieRepository.findById(id)
            .map(existingMovie -> {
                movie.setId(id);
                return movieRepository.save(movie);
            });
    }
}
