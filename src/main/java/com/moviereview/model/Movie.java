package com.moviereview.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;
    @Column(columnDefinition = "TEXT")
    @Size(max = 5000, message = "Description must be less than 5000 characters")
    private String description;
    private LocalDate releaseDate;
    @Size(max = 255, message = "Director name must be less than 255 characters")
    private String director;
    @Size(max = 100, message = "Genre must be less than 100 characters")
    private String genre;

    private String posterUrl; // URL to the movie poster

    private Integer duration; // Movie duration in minutes

    @Column(columnDefinition = "DECIMAL(3,2) DEFAULT 0.0")
    private Double avgRating = 0.0; // Average rating

    // One-to-many relationship with reviews
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews;

  
}