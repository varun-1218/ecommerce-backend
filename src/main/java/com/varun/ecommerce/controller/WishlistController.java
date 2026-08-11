package com.varun.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.varun.ecommerce.dto.WishlistDTO;
import com.varun.ecommerce.service.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*")
public class WishlistController {
    
    @Autowired
    private WishlistService wishlistService;
    
    @PostMapping("/add")
    public ResponseEntity<WishlistDTO> addToWishlist(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        try {
            WishlistDTO wishlistDTO = wishlistService.addToWishlist(userId, productId);
            return ResponseEntity.status(HttpStatus.CREATED).body(wishlistDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromWishlist(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        try {
            wishlistService.removeFromWishlist(userId, productId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WishlistDTO>> getWishlistByUser(@PathVariable Long userId) {
        try {
            List<WishlistDTO> wishlist = wishlistService.getWishlistByUserId(userId);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkProductInWishlist(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        try {
            boolean exists = wishlistService.isProductInWishlist(userId, productId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
    
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearWishlist(@PathVariable Long userId) {
        try {
            wishlistService.clearWishlist(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}