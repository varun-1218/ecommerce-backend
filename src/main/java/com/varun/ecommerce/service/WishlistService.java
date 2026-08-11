package com.varun.ecommerce.service;

import java.util.List;

import com.varun.ecommerce.dto.WishlistDTO;

public interface WishlistService {
    
    WishlistDTO addToWishlist(Long userId, Long productId);
    
    void removeFromWishlist(Long userId, Long productId);
    
    List<WishlistDTO> getWishlistByUserId(Long userId);
    
    boolean isProductInWishlist(Long userId, Long productId);
    
    void clearWishlist(Long userId);
}