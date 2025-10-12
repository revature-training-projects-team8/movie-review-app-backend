package com.moviereview.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "title must not be blank")
    @Size(max = 255, message = "title must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String title;

    @Size(max = 2000, message = "description must not exceed 2000 characters")
    @Column(length = 2000)
    private String description;

    @Min(value = 1888, message = "releaseYear must be >= 1888")
    @Max(value = 2100, message = "releaseYear must be <= 2100")
    private Integer releaseYear;

    @NotBlank(message = "genre must not be blank")
    @Size(max = 100, message = "genre must not exceed 100 characters")
    @Column(length = 100)
    private String genre;

    @NotBlank(message = "director must not be blank")
    @Size(max = 255, message = "director must not exceed 255 characters")
    @Column(length = 255)
    private String director;

    // Basic URL validation (allows http/https). For stricter validation install hibernate-validator and use @URL
    @Size(max = 2048, message = "posterUrl must not exceed 2048 characters")
    @Pattern(regexp = "^(https?://).+", message = "posterUrl must be a valid http(s) URL")
    @Column(length = 2048)
    private String posterUrl;

    @DecimalMin(value = "0.0", inclusive = true, message = "averageRating must be >= 0.0")
    @DecimalMax(value = "10.0", inclusive = true, message = "averageRating must be <= 10.0")
    private Double averageRating;
}
