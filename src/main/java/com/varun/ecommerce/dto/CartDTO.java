package com.varun.ecommerce.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartDTO {
    
    private Long id;
    private Long userId;
    private List<CartItemDTO> cartItems = new ArrayList<>();
    private BigDecimal totalPrice;
    
    public CartDTO() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public List<CartItemDTO> getCartItems() {
        return cartItems;
    }
    
    public void setCartItems(List<CartItemDTO> cartItems) {
        this.cartItems = cartItems;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}