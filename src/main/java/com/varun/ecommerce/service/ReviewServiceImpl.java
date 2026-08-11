package com.varun.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.varun.ecommerce.dto.ReviewDTO;
import com.varun.ecommerce.entity.Order;
import com.varun.ecommerce.entity.Product;
import com.varun.ecommerce.entity.Review;
import com.varun.ecommerce.entity.User;
import com.varun.ecommerce.repository.OrderRepository;
import com.varun.ecommerce.repository.ProductRepository;
import com.varun.ecommerce.repository.ReviewRepository;
import com.varun.ecommerce.repository.UserRepository;

@Service
public class ReviewServiceImpl implements ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO, Long userId) {
        Product product = productRepository.findById(reviewDTO.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user has ordered this product (optional validation)
        // You can add this check if you want only customers who purchased to review
        
        // Check if user already reviewed this product
        List<Review> existingReviews = reviewRepository.findByProductAndUser(product, user);
        if (!existingReviews.isEmpty()) {
            throw new RuntimeException("You have already reviewed this product");
        }
        
        Order order = null;
        if (reviewDTO.getOrderId() != null) {
            order = orderRepository.findById(reviewDTO.getOrderId())
                .orElse(null);
        }
        
        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setProduct(product);
        review.setUser(user);
        review.setOrder(order);
        
        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview);
    }
    
    @Override
    public ReviewDTO updateReview(Long reviewId, ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        
        Review updatedReview = reviewRepository.save(review);
        return convertToDTO(updatedReview);
    }
    
    @Override
    public ReviewDTO getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        return convertToDTO(review);
    }
    
    @Override
    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ReviewDTO> getReviewsByUserId(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Review> reviews = reviewRepository.findByUser(user);
        return reviews.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        reviewRepository.delete(review);
    }
    
    @Override
    public Double getAverageRatingForProduct(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double sum = reviews.stream()
            .mapToInt(Review::getRating)
            .sum();
        
        return sum / reviews.size();
    }
    
    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setId(review.getId());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setComment(review.getComment());
        reviewDTO.setProductId(review.getProduct().getId());
        reviewDTO.setProductName(review.getProduct().getName());
        reviewDTO.setUserId(review.getUser().getId());
        reviewDTO.setUserName(review.getUser().getFirstName() + " " + review.getUser().getLastName());
        reviewDTO.setCreatedAt(review.getCreatedAt());
        
        if (review.getOrder() != null) {
            reviewDTO.setOrderId(review.getOrder().getId());
        }
        
        return reviewDTO;
    }
}