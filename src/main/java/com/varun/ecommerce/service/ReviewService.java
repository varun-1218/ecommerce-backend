package com.varun.ecommerce.service;

import java.util.List;

import com.varun.ecommerce.dto.ReviewDTO;

public interface ReviewService {
    
    ReviewDTO createReview(ReviewDTO reviewDTO, Long userId);
    
    ReviewDTO updateReview(Long reviewId, ReviewDTO reviewDTO);
    
    ReviewDTO getReviewById(Long reviewId);
    
    List<ReviewDTO> getReviewsByProductId(Long productId);
    
    List<ReviewDTO> getReviewsByUserId(Long userId);
    
    void deleteReview(Long reviewId);
    
    Double getAverageRatingForProduct(Long productId);
}