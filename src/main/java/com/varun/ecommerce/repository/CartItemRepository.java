package com.varun.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.varun.ecommerce.entity.Cart;
import com.varun.ecommerce.entity.CartItem;
import com.varun.ecommerce.entity.Product;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    
    void deleteByCartId(Long cartId);
}
