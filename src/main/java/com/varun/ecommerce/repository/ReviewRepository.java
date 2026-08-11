package com.varun.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.varun.ecommerce.entity.Product;
import com.varun.ecommerce.entity.Review;
import com.varun.ecommerce.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByProduct(Product product);
    
    List<Review> findByProductId(Long productId);
    
    List<Review> findByUser(User user);
    
    List<Review> findByProductAndUser(Product product, User user);
}