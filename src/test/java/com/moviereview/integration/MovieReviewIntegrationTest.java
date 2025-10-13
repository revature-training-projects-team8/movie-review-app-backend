package com.moviereview.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviereview.dto.MovieDTO;
import com.moviereview.dto.ReviewDTO;
import com.moviereview.model.Movie;
import com.moviereview.model.Review;
import com.moviereview.model.User;
import com.moviereview.repository.MovieRepository;
import com.moviereview.repository.ReviewRepository;
import com.moviereview.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@DisplayName("Integration Tests - Complete User Stories")
class MovieReviewIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private Movie testMovie;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Set up MockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clean repositories
        reviewRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();

        // Create test movie
        testMovie = new Movie();
        testMovie.setTitle("The Matrix");
        testMovie.setDescription("A computer hacker learns about the true nature of reality.");
        testMovie.setReleaseDate(LocalDate.of(1999, 3, 31));
        testMovie.setDirector("The Wachowskis");
        testMovie.setGenre("Sci-Fi");
        testMovie.setPosterUrl("https://example.com/matrix-poster.jpg");
        testMovie = movieRepository.save(testMovie);

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Complete User Story A.1 & A.2 - Browse and view movie details")
    void completeMovieBrowsingFlow() throws Exception {
        // Story A.1 - Browse Movies: View all movies
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("The Matrix")))
                .andExpect(jsonPath("$[0].averageRating", is(0.0))); // No reviews yet

        // Story A.1 - Search movies by title
        mockMvc.perform(get("/api/movies/search")
                .param("query", "matrix"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("The Matrix")));

        // Story A.2 - View detailed movie information
        mockMvc.perform(get("/api/movies/" + testMovie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("The Matrix")))
                .andExpect(jsonPath("$.description", containsString("computer hacker")))
                .andExpect(jsonPath("$.director", is("The Wachowskis")))
                .andExpect(jsonPath("$.genre", is("Sci-Fi")))
                .andExpect(jsonPath("$.averageRating", is(0.0)));
    }

    @Test
    @DisplayName("Complete User Story B.1 - User Authentication Flow")
    void completeUserAuthenticationFlow() throws Exception {
        // Story B.1 - Register new user
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.id", notNullValue()));

        // Story B.1 - Login with valid credentials
        Map<String, String> loginCredentials = new HashMap<>();
        loginCredentials.put("username", "newuser");
        loginCredentials.put("password", "password123");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginCredentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newuser")));

        // Story B.1 - Reject invalid login
        Map<String, String> invalidCredentials = new HashMap<>();
        invalidCredentials.put("username", "newuser");
        invalidCredentials.put("password", "wrongpassword");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCredentials)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Complete User Story B.2 & B.3 - Review Management Flow")
    void completeReviewManagementFlow() throws Exception {
        // Story B.2 - Submit a review
        ReviewDTO reviewDto = new ReviewDTO();
        reviewDto.setRating(5);
        reviewDto.setComment("Amazing movie! Mind-bending plot.");

        mockMvc.perform(post("/api/reviews/movie/" + testMovie.getId() + "/user/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating", is(5)))
                .andExpect(jsonPath("$.comment", is("Amazing movie! Mind-bending plot.")))
                .andExpect(jsonPath("$.movieTitle", is("The Matrix")))
                .andExpect(jsonPath("$.username", is("testuser")));

        // Verify review appears in movie's reviews
        mockMvc.perform(get("/api/reviews/movie/" + testMovie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].rating", is(5)))
                .andExpect(jsonPath("$[0].comment", is("Amazing movie! Mind-bending plot.")));

        // Verify movie now has average rating
        mockMvc.perform(get("/api/movies/" + testMovie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating", is(5.0)));

        // Story B.3 - View user's own reviews
        mockMvc.perform(get("/api/reviews/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].movieTitle", is("The Matrix")));

        // Get the review ID for update/delete operations
        Review savedReview = reviewRepository.findAll().get(0);

        // Story B.3 - Edit own review
        ReviewDTO updatedReviewDto = new ReviewDTO();
        updatedReviewDto.setRating(4);
        updatedReviewDto.setComment("Good movie, but not perfect.");

        mockMvc.perform(put("/api/reviews/" + savedReview.getId() + "/user/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedReviewDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating", is(4)))
                .andExpect(jsonPath("$.comment", is("Good movie, but not perfect.")));

        // Verify updated average rating
        mockMvc.perform(get("/api/movies/" + testMovie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating", is(4.0)));

        // Story B.3 - Delete own review
        mockMvc.perform(delete("/api/reviews/" + savedReview.getId() + "/user/" + testUser.getId()))
                .andExpect(status().isNoContent());

        // Verify review is deleted
        mockMvc.perform(get("/api/reviews/movie/" + testMovie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Verify average rating is back to 0
        mockMvc.perform(get("/api/movies/" + testMovie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating", is(0.0)));
    }

    @Test
    @DisplayName("Complete User Story C.1 - Admin Movie Management")
    void compliteAdminMovieManagement() throws Exception {
        // Story C.1 - Admin adds new movie
        MovieDTO newMovieDto = new MovieDTO();
        newMovieDto.setTitle("Inception");
        newMovieDto.setDescription("A thief who steals corporate secrets through dream-sharing technology.");
        newMovieDto.setReleaseDate(LocalDate.of(2010, 7, 16));
        newMovieDto.setDirector("Christopher Nolan");
        newMovieDto.setGenre("Sci-Fi");
        newMovieDto.setPosterUrl("https://example.com/inception-poster.jpg");

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMovieDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Inception")))
                .andExpect(jsonPath("$.director", is("Christopher Nolan")));

        // Verify movie appears in browse list
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", hasItems("The Matrix", "Inception")));

        // Get the new movie ID
        Movie savedInception = movieRepository.findByTitleContainingIgnoreCase("Inception").get(0);

        // Story C.1 - Admin updates movie
        MovieDTO updateDto = new MovieDTO();
        updateDto.setTitle("Inception - Director's Cut");
        updateDto.setDescription("Extended version of the mind-bending thriller.");
        updateDto.setReleaseDate(LocalDate.of(2010, 7, 16));
        updateDto.setDirector("Christopher Nolan");
        updateDto.setGenre("Sci-Fi Thriller");
        updateDto.setPosterUrl("https://example.com/inception-dc-poster.jpg");

        mockMvc.perform(put("/api/movies/" + savedInception.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Inception - Director's Cut")))
                .andExpect(jsonPath("$.genre", is("Sci-Fi Thriller")));

        // Story C.1 - Admin deletes movie
        mockMvc.perform(delete("/api/movies/" + savedInception.getId()))
                .andExpect(status().isNoContent());

        // Verify movie is deleted
        mockMvc.perform(get("/api/movies/" + savedInception.getId()))
                .andExpect(status().isNotFound());

        // Verify movie list is back to original size
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("The Matrix")));
    }

    @Test
    @DisplayName("Validation and Error Handling")
    void validationAndErrorHandling() throws Exception {
        // Test movie validation
        MovieDTO invalidMovie = new MovieDTO();
        invalidMovie.setTitle(""); // Invalid - empty title
        invalidMovie.setDescription("Valid description");

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Title is required")));

        // Test review rating validation
        ReviewDTO invalidReview = new ReviewDTO();
        invalidReview.setRating(6); // Invalid - outside 1-5 range
        invalidReview.setComment("Valid comment");

        mockMvc.perform(post("/api/reviews/movie/" + testMovie.getId() + "/user/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReview)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Rating must be at most 5")));

        // Test user validation
        User invalidUser = new User();
        invalidUser.setUsername("ab"); // Invalid - too short
        invalidUser.setPassword("123"); // Invalid - too short

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Username must be between 3 and 50 characters")));
    }

    @Test
    @DisplayName("CORS Support for Frontend Integration")
    void corsSupport() throws Exception {
        mockMvc.perform(get("/api/movies")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }
}