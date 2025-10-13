package com.moviereview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviereview.dto.MovieDTO;

import com.moviereview.model.Movie;
import com.moviereview.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@DisplayName("Movie Controller Tests")
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie testMovie;
    private List<Movie> testMovies;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("The Matrix");
        testMovie.setDescription("A computer hacker learns about the true nature of reality.");
        testMovie.setReleaseDate(LocalDate.of(1999, 3, 31));
        testMovie.setDirector("The Wachowskis");
        testMovie.setGenre("Sci-Fi");
        testMovie.setPosterUrl("https://example.com/matrix-poster.jpg");

        Movie testMovie2 = new Movie();
        testMovie2.setId(2L);
        testMovie2.setTitle("Inception");
        testMovie2.setDescription("A thief enters dreams to steal secrets.");
        testMovie2.setReleaseDate(LocalDate.of(2010, 7, 16));
        testMovie2.setDirector("Christopher Nolan");
        testMovie2.setGenre("Sci-Fi");
        testMovie2.setPosterUrl("https://example.com/inception-poster.jpg");

        testMovies = Arrays.asList(testMovie, testMovie2);
    }

    @Test
    @DisplayName("A.1 - Should get all movies for browsing")
    void getAllMovies_ShouldReturnMovieList() throws Exception {
        // Given
        when(movieService.getAllMovies()).thenReturn(testMovies);
        when(movieService.getAverageRatingForMovie(anyLong())).thenReturn(4.5);

        // When & Then
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("The Matrix"))
                .andExpect(jsonPath("$[0].averageRating").value(4.5))
                .andExpect(jsonPath("$[1].title").value("Inception"));

        verify(movieService).getAllMovies();
        verify(movieService, times(2)).getAverageRatingForMovie(anyLong());
    }

    @Test
    @DisplayName("A.1 - Should search movies by title or genre")
    void searchMovies_ShouldReturnFilteredResults() throws Exception {
        // Given
        String searchQuery = "matrix";
        List<Movie> searchResults = Arrays.asList(testMovie);
        when(movieService.searchMovies(searchQuery)).thenReturn(searchResults);
        when(movieService.getAverageRatingForMovie(1L)).thenReturn(4.5);

        // When & Then
        mockMvc.perform(get("/api/movies/search")
                .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("The Matrix"));

        verify(movieService).searchMovies(searchQuery);
    }

    @Test
    @DisplayName("A.2 - Should get movie details by ID")
    void getMovieById_ShouldReturnMovieDetails() throws Exception {
        // Given
        when(movieService.getMovieById(1L)).thenReturn(Optional.of(testMovie));
        when(movieService.getAverageRatingForMovie(1L)).thenReturn(4.5);

        // When & Then
        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("The Matrix"))
                .andExpect(
                        jsonPath("$.description").value("A computer hacker learns about the true nature of reality."))
                .andExpect(jsonPath("$.director").value("The Wachowskis"))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"))
                .andExpect(jsonPath("$.averageRating").value(4.5));

        verify(movieService).getMovieById(1L);
        verify(movieService).getAverageRatingForMovie(1L);
    }

    @Test
    @DisplayName("A.2 - Should return 404 when movie not found")
    void getMovieById_WhenNotFound_ShouldReturn404() throws Exception {
        // Given
        when(movieService.getMovieById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/movies/999"))
                .andExpect(status().isNotFound());

        verify(movieService).getMovieById(999L);
    }

    @Test
    @DisplayName("C.1 - Admin should be able to add a new movie")
    void addMovie_ShouldCreateNewMovie() throws Exception {
        // Given
        MovieDTO movieDto = new MovieDTO();
        movieDto.setTitle("New Movie");
        movieDto.setDescription("A new movie description");
        movieDto.setReleaseDate(LocalDate.of(2023, 1, 1));
        movieDto.setDirector("New Director");
        movieDto.setGenre("Drama");

        Movie savedMovie = new Movie();
        savedMovie.setId(3L);
        savedMovie.setTitle("New Movie");
        savedMovie.setDescription("A new movie description");

        when(movieService.saveMovie(any(Movie.class))).thenReturn(savedMovie);
        when(movieService.getAverageRatingForMovie(3L)).thenReturn(0.0);

        // When & Then
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("New Movie"));

        verify(movieService).saveMovie(any(Movie.class));
    }

    @Test
    @DisplayName("C.1 - Should validate movie data when adding")
    void addMovie_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given - Empty title (violates @NotBlank)
        MovieDTO invalidDto = new MovieDTO();
        invalidDto.setTitle(""); // Invalid - empty title
        invalidDto.setDescription("Valid description");

        // When & Then
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).saveMovie(any(Movie.class));
    }

    @Test
    @DisplayName("C.1 - Admin should be able to update a movie")
    void updateMovie_ShouldUpdateExistingMovie() throws Exception {
        // Given
        Long movieId = 1L;
        MovieDTO updateDto = new MovieDTO();
        updateDto.setTitle("Updated Matrix");
        updateDto.setDescription("Updated description");
        updateDto.setDirector("Updated Director");

        when(movieService.getMovieById(movieId)).thenReturn(Optional.of(testMovie));
        when(movieService.saveMovie(any(Movie.class))).thenReturn(testMovie);
        when(movieService.getAverageRatingForMovie(movieId)).thenReturn(4.5);

        // When & Then
        mockMvc.perform(put("/api/movies/" + movieId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Matrix"));

        verify(movieService).getMovieById(movieId);
        verify(movieService).saveMovie(any(Movie.class));
    }

    @Test
    @DisplayName("C.1 - Admin should be able to delete a movie")
    void deleteMovie_ShouldDeleteExistingMovie() throws Exception {
        // Given
        Long movieId = 1L;
        when(movieService.getMovieById(movieId)).thenReturn(Optional.of(testMovie));

        // When & Then
        mockMvc.perform(delete("/api/movies/" + movieId))
                .andExpect(status().isNoContent());

        verify(movieService).getMovieById(movieId);
        verify(movieService).deleteMovie(movieId);
    }

    @Test
    @DisplayName("Should handle CORS for frontend integration")
    void corsHeaders_ShouldBePresent() throws Exception {
        // Given
        when(movieService.getAllMovies()).thenReturn(testMovies);
        when(movieService.getAverageRatingForMovie(anyLong())).thenReturn(4.5);

        // When & Then
        mockMvc.perform(get("/api/movies")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }
}