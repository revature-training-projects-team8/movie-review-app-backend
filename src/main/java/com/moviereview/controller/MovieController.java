package com.moviereview.controller;

import com.moviereview.dto.MovieDTO;
import com.moviereview.exception.ResourceNotFoundException;
import com.moviereview.model.Movie;
import com.moviereview.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" })
public class MovieController {

    @Autowired
    private MovieService movieService;

    // Browse all movies
    @GetMapping
    public List<MovieDTO> getAllMovies() {
        return movieService.getAllMovies().stream()
                .map(this::convertToDtoWithAverageRating)
                .collect(Collectors.toList());
    }

    // Search movies
    @GetMapping("/search")
    public List<MovieDTO> searchMovies(@RequestParam String query) {
        return movieService.searchMovies(query).stream()
                .map(this::convertToDtoWithAverageRating)
                .collect(Collectors.toList());
    }

    // View movie details by ID
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return ResponseEntity.ok(convertToDtoWithAverageRating(movie));
    }

    // Add a new movie
    @PostMapping
    public ResponseEntity<MovieDTO> addMovie(@Valid @RequestBody MovieDTO movieDto) {
        Movie movie = convertToEntity(movieDto);
        Movie savedMovie = movieService.saveMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDtoWithAverageRating(savedMovie));
    }

    // Update a movie
    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieDTO movieDto) {
        Movie existingMovie = movieService.getMovieById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        existingMovie.setTitle(movieDto.getTitle());
        existingMovie.setDescription(movieDto.getDescription());
        existingMovie.setReleaseDate(movieDto.getReleaseDate());
        existingMovie.setDirector(movieDto.getDirector());
        existingMovie.setGenre(movieDto.getGenre());
        existingMovie.setPosterUrl(movieDto.getPosterUrl());

        Movie updatedMovie = movieService.saveMovie(existingMovie);
        return ResponseEntity.ok(convertToDtoWithAverageRating(updatedMovie));
    }

    // Delete a movie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        // Check if movie exists before deleting
        movieService.getMovieById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    // Helper to convert Movie entity to DTO and include average rating
    private MovieDTO convertToDtoWithAverageRating(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setDirector(movie.getDirector());
        dto.setGenre(movie.getGenre());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setAverageRating(movieService.getAverageRatingForMovie(movie.getId()));
        return dto;
    }

    // Helper to convert DTO to entity
    private Movie convertToEntity(MovieDTO dto) {
        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setDirector(dto.getDirector());
        movie.setGenre(dto.getGenre());
        movie.setPosterUrl(dto.getPosterUrl());
        return movie;
    }
}
