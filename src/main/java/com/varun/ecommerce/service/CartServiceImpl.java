package com.varun.ecommerce.service;

import java.math.BigDecimal;
import java.time.LocalDate;   // <-- ADD THIS
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
        
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStockQuantity());
        }
        
        Cart cart = cartRepository.findByUser(user)
            .orElseGet(() -> {
                Cart newCart = new Cart(user);
                return cartRepository.save(newCart);
            });
        
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);
        
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            cartItemRepository.save(cartItem);
        }
        
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

        if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Coupon has expired");
        }

        if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new RuntimeException("Coupon usage limit reached");
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountPercentage() != null) {
            discount = cart.getTotalPrice()
                .multiply(coupon.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100));
        } else if (coupon.getDiscountAmount() != null) {
            discount = coupon.getDiscountAmount();
        }

        if (discount.compareTo(cart.getTotalPrice()) > 0) discount = cart.getTotalPrice();

        cart.setTotalPrice(cart.getTotalPrice().subtract(discount));
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
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