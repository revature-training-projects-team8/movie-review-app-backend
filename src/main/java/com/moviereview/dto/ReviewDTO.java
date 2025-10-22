package com.moviereview.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long movieId;
    private String movieTitle; // For display in user's reviews
    private Long userId;
    private String username; // For display on movie detail page
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    @Size(max = 2000, message = "Comment must be less than 2000 characters")
    private String comment;
    private LocalDateTime reviewDate;

}