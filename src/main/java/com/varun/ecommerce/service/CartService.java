package com.varun.ecommerce.service;

import com.varun.ecommerce.dto.CartDTO;

public interface CartService {
    
    CartDTO addToCart(Long userId, Long productId, Integer quantity);
    
    CartDTO getCartByUserId(Long userId);
    
    CartDTO updateCartItemQuantity(Long userId, Long productId, Integer quantity);
    
    void removeFromCart(Long userId, Long productId);
    
    void clearCart(Long userId);
    
    CartDTO applyCoupon(Long userId, String couponCode);
}