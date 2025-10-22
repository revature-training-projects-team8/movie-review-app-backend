package com.moviereview.repository;

import com.moviereview.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByGenreContainingIgnoreCase(String genre);

    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Movie> searchMovies(@Param("searchTerm") String searchTerm);
}
