package com.moviereview.service;

import com.moviereview.model.Review;
import com.moviereview.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> findByMovieId(Long movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public Optional<Review> update(Long id, Review review) {
        return reviewRepository.findById(id)
                .map(existingReview -> {
                    review.setId(id);
                    return reviewRepository.save(review);
                });
    }

    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
}