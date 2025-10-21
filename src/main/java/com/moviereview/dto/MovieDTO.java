package com.moviereview.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    private Long id;
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;
    @Size(max = 5000, message = "Description must be less than 5000 characters")
    private String description;
    private LocalDate releaseDate;
    private String director;
    private String genre;
    private String posterUrl;
    private Integer duration; // Duration in minutes
    private double averageRating; // Calculated field

    
}