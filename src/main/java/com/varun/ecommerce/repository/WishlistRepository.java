package com.varun.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.varun.ecommerce.entity.Product;
import com.varun.ecommerce.entity.User;
import com.varun.ecommerce.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    
    List<Wishlist> findByUser(User user);
    
    List<Wishlist> findByUserId(Long userId);
    
    Optional<Wishlist> findByUserAndProduct(User user, Product product);
    
    boolean existsByUserAndProduct(User user, Product product);
}