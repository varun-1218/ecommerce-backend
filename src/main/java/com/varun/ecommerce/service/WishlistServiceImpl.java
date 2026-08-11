package com.varun.ecommerce.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.varun.ecommerce.dto.WishlistDTO;
import com.varun.ecommerce.entity.Product;
import com.varun.ecommerce.entity.User;
import com.varun.ecommerce.entity.Wishlist;
import com.varun.ecommerce.repository.ProductRepository;
import com.varun.ecommerce.repository.UserRepository;
import com.varun.ecommerce.repository.WishlistRepository;

@Service
public class WishlistServiceImpl implements WishlistService {
    
    @Autowired
    private WishlistRepository wishlistRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    @Transactional
    public WishlistDTO addToWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if already in wishlist
        Optional<Wishlist> existingWishlist = wishlistRepository.findByUserAndProduct(user, product);
        if (existingWishlist.isPresent()) {
            throw new RuntimeException("Product already in wishlist");
        }
        
        Wishlist wishlist = new Wishlist(user, product);
        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        
        return convertToDTO(savedWishlist);
    }
    
    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Wishlist wishlist = wishlistRepository.findByUserAndProduct(user, product)
            .orElseThrow(() -> new RuntimeException("Product not found in wishlist"));
        
        wishlistRepository.delete(wishlist);
    }
    
    @Override
    public List<WishlistDTO> getWishlistByUserId(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Wishlist> wishlists = wishlistRepository.findByUser(user);
        return wishlists.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean isProductInWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        return wishlistRepository.existsByUserAndProduct(user, product);
    }
    
    @Override
    @Transactional
    public void clearWishlist(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Wishlist> wishlists = wishlistRepository.findByUser(user);
        wishlistRepository.deleteAll(wishlists);
    }
    
    private WishlistDTO convertToDTO(Wishlist wishlist) {
        WishlistDTO wishlistDTO = new WishlistDTO();
        wishlistDTO.setId(wishlist.getId());
        wishlistDTO.setUserId(wishlist.getUser().getId());
        wishlistDTO.setUserName(wishlist.getUser().getFirstName() + " " + wishlist.getUser().getLastName());
        wishlistDTO.setProductId(wishlist.getProduct().getId());
        wishlistDTO.setProductName(wishlist.getProduct().getName());
        wishlistDTO.setProductPrice(wishlist.getProduct().getPrice());
        wishlistDTO.setProductImageUrl(wishlist.getProduct().getImageUrl());
        wishlistDTO.setAddedDate(wishlist.getAddedDate());
        return wishlistDTO;
    }
}