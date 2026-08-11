package com.varun.ecommerce.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.varun.ecommerce.dto.CartDTO;
import com.varun.ecommerce.dto.CartItemDTO;
import com.varun.ecommerce.entity.Cart;
import com.varun.ecommerce.entity.CartItem;
import com.varun.ecommerce.entity.Coupon;
import com.varun.ecommerce.entity.Product;
import com.varun.ecommerce.entity.User;
import com.varun.ecommerce.repository.CartItemRepository;
import com.varun.ecommerce.repository.CartRepository;
import com.varun.ecommerce.repository.CouponRepository;
import com.varun.ecommerce.repository.ProductRepository;
import com.varun.ecommerce.repository.UserRepository;

@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Override
    @Transactional
    public CartDTO addToCart(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check stock availability
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStockQuantity());
        }
        
        // Get or create cart for user
        Cart cart = cartRepository.findByUser(user)
            .orElseGet(() -> {
                Cart newCart = new Cart(user);
                return cartRepository.save(newCart);
            });
        
        // Check if product already exists in cart
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);
        
        if (existingCartItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItemRepository.save(cartItem);
        } else {
            // Add new item
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            cartItemRepository.save(cartItem);
        }
        
        // Update cart total
        updateCartTotal(cart);
        
        return convertToDTO(cart);
    }
    
    @Override
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found for user"));
        return convertToDTO(cart);
    }
    
    @Override
    @Transactional
    public CartDTO updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            cartItemRepository.save(cartItem);
        }
        
        updateCartTotal(cart);
        
        return convertToDTO(cart);
    }
    
    @Override
    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        cartItemRepository.delete(cartItem);
        updateCartTotal(cart);
    }
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }
    
    @Override
    @Transactional
    public CartDTO applyCoupon(Long userId, String couponCode) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        Coupon coupon = couponRepository.findByCode(couponCode)
            .orElseThrow(() -> new RuntimeException("Invalid coupon code"));
        
        // Check coupon validity
        if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new RuntimeException("Coupon usage limit reached");
        }
        
        // Apply discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (coupon.getDiscountPercentage() != null) {
            discountAmount = cart.getTotalPrice()
                .multiply(coupon.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100));
        } else if (coupon.getDiscountAmount() != null) {
            discountAmount = coupon.getDiscountAmount();
        }
        
        // Update coupon usage
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
        
        // Apply discount to cart (in real implementation, you might store discount separately)
        cart.setTotalPrice(cart.getTotalPrice().subtract(discountAmount));
        cartRepository.save(cart);
        
        return convertToDTO(cart);
    }
    
    private void updateCartTotal(Cart cart) {
        BigDecimal total = cart.getCartItems().stream()
            .map(CartItem::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
        cartRepository.save(cart);
    }
    
    private CartDTO convertToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setUserId(cart.getUser().getId());
        cartDTO.setTotalPrice(cart.getTotalPrice());
        
        // Convert cart items
        cart.getCartItems().forEach(cartItem -> {
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setId(cartItem.getId());
            cartItemDTO.setProductId(cartItem.getProduct().getId());
            cartItemDTO.setProductName(cartItem.getProduct().getName());
            cartItemDTO.setProductPrice(cartItem.getProduct().getPrice());
            cartItemDTO.setProductImageUrl(cartItem.getProduct().getImageUrl());
            cartItemDTO.setQuantity(cartItem.getQuantity());
            cartItemDTO.setPrice(cartItem.getPrice());
            cartDTO.getCartItems().add(cartItemDTO);
        });
        
        return cartDTO;
    }
}