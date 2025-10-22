package com.moviereview.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonIgnoreProperties({"reviews"})
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"reviews"})
    private User user;
    @Column(nullable = false)
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating; // 1-5 stars
    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "Comment must be less than 2000 characters")
    private String comment;
    private LocalDateTime reviewDate;

    @PrePersist
    protected void onCreate() {
        reviewDate = LocalDateTime.now();
    }

}