package com.moviereview.controller;

import com.moviereview.dto.MovieDTO;
import com.moviereview.model.Movie;
import com.moviereview.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Movies Controller (without /api prefix)
 * Provides compatibility for frontend calls to /movies directly
 * Delegates to MovieService for business logic
 */
@RestController
@RequestMapping("/movies")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" }, allowCredentials = "true")
@RequiredArgsConstructor
public class MoviesController {

    private final MovieService movieService;

    /**
     * Get all movies
     * GET /movies
     */
    @GetMapping
    public List<MovieDTO> getAllMovies() {
        return movieService.getAllMovies().stream()
                .map(this::convertToDtoWithAverageRating)
                .collect(Collectors.toList());
    }

    /**
     * Get movie by ID
     * GET /movies/{id}
     */
    @GetMapping("/{id}")
    public MovieDTO getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        return convertToDtoWithAverageRating(movie);
    }

    /**
     * Search movies
     * GET /movies/search?query=keyword
     */
    @GetMapping("/search")
    public List<MovieDTO> searchMovies(@RequestParam String query) {
        return movieService.searchMovies(query).stream()
                .map(this::convertToDtoWithAverageRating)
                .collect(Collectors.toList());
    }

    /**
     * Create a new movie
     * POST /movies
     */
    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@Valid @RequestBody MovieDTO movieDto) {
        Movie movie = convertToEntity(movieDto);
        Movie savedMovie = movieService.saveMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDtoWithAverageRating(savedMovie));
    }

    /**
     * Update an existing movie
     * PUT /movies/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieDTO movieDto) {
        System.out.println("🔍 Updating movie with ID: " + id);
        System.out.println("📅 Release Date received: " + movieDto.getReleaseDate());
        System.out.println("🎬 Title: " + movieDto.getTitle());
        System.out.println("🖼️ Poster URL: " + movieDto.getPosterUrl());

        Movie movie = movieService.getMovieById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));

        // Update fields only if they are not null
        if (movieDto.getTitle() != null) {
            movie.setTitle(movieDto.getTitle());
        }
        if (movieDto.getDescription() != null) {
            movie.setDescription(movieDto.getDescription());
        }
        if (movieDto.getReleaseDate() != null) {
            movie.setReleaseDate(movieDto.getReleaseDate());
        }
        if (movieDto.getDirector() != null) {
            movie.setDirector(movieDto.getDirector());
        }
        if (movieDto.getGenre() != null) {
            movie.setGenre(movieDto.getGenre());
        }
        if (movieDto.getPosterUrl() != null) {
            movie.setPosterUrl(movieDto.getPosterUrl());
        }
        if (movieDto.getDuration() != null) {
            movie.setDuration(movieDto.getDuration());
        }

        Movie updatedMovie = movieService.saveMovie(movie);
        System.out.println("✅ Movie updated successfully. Release Date in DB: " + updatedMovie.getReleaseDate());
        return ResponseEntity.ok(convertToDtoWithAverageRating(updatedMovie));
    }

    /**
     * Delete a movie
     * DELETE /movies/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper to convert DTO to Movie entity
     */
    private Movie convertToEntity(MovieDTO dto) {
        Movie movie = new Movie();
        if (dto.getId() != null) {
            movie.setId(dto.getId());
        }
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setDirector(dto.getDirector());
        movie.setGenre(dto.getGenre());
        movie.setPosterUrl(dto.getPosterUrl());
        movie.setDuration(dto.getDuration());
        return movie;
    }

    /**
     * Helper to convert Movie entity to DTO and include average rating
     */
    private MovieDTO convertToDtoWithAverageRating(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setDirector(movie.getDirector());
        dto.setGenre(movie.getGenre());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setDuration(movie.getDuration());
        dto.setAverageRating(movieService.getAverageRatingForMovie(movie.getId()));
        return dto;
    }
}
