package com.moviereview.repository;

import com.moviereview.model.Movie;
import com.moviereview.model.Review;
import com.moviereview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovie(Movie movie);

    List<Review> findByUser(User user);

    Optional<Review> findByMovieAndUser(Movie movie, User user); // To check if user already reviewed
}