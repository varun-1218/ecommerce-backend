package com.varun.ecommerce.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.varun.ecommerce.dto.OrderDTO;
import com.varun.ecommerce.dto.OrderItemDTO;
import com.varun.ecommerce.entity.Cart;
import com.varun.ecommerce.entity.CartItem;
import com.varun.ecommerce.entity.Coupon;
import com.varun.ecommerce.entity.Order;
import com.varun.ecommerce.entity.OrderItem;
import com.varun.ecommerce.entity.Product;
import com.varun.ecommerce.entity.User;
import com.varun.ecommerce.repository.CartItemRepository;
import com.varun.ecommerce.repository.CartRepository;
import com.varun.ecommerce.repository.CouponRepository;
import com.varun.ecommerce.repository.OrderItemRepository;
import com.varun.ecommerce.repository.OrderRepository;
import com.varun.ecommerce.repository.ProductRepository;
import com.varun.ecommerce.repository.UserRepository;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Override
    @Transactional
    public OrderDTO placeOrder(Long userId, String shippingAddress, String paymentMethod, String couponCode) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart is empty"));
        
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot place order with empty cart");
        }
        
        // Check stock availability
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }
        
        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setOrderTrackingNumber(generateTrackingNumber());
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());
        
        // Apply coupon if provided
        if (couponCode != null && !couponCode.isEmpty()) {
            Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));
            
            if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
                throw new RuntimeException("Coupon usage limit reached");
            }
            
            order.setCoupon(coupon);
            
            // Calculate discount
            BigDecimal discount = BigDecimal.ZERO;
            if (coupon.getDiscountPercentage() != null) {
                discount = cart.getTotalPrice()
                    .multiply(coupon.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100));
            } else if (coupon.getDiscountAmount() != null) {
                discount = coupon.getDiscountAmount();
            }
            
            order.setDiscountAmount(discount);
            order.setTotalPrice(cart.getTotalPrice().subtract(discount));
            
            // Update coupon usage
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        } else {
            order.setTotalPrice(cart.getTotalPrice());
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // Create order items and update stock
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItemRepository.save(orderItem);
            
            // Update product stock
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        // Clear cart
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
        
        return convertToDTO(savedOrder);
    }
    
    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }
    
    @Override
    public OrderDTO getOrderByTrackingNumber(String trackingNumber) {
        Order order = orderRepository.findByOrderTrackingNumber(trackingNumber)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }
    
    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAllOrdersSortedByDate();
        return orders.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        
        return convertToDTO(updatedOrder);
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }
        
        // Restore product stock
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }
        
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }
    
    private String generateTrackingNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderTrackingNumber(order.getOrderTrackingNumber());
        orderDTO.setUserId(order.getUser().getId());
        orderDTO.setUserName(order.getUser().getFirstName() + " " + order.getUser().getLastName());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setShippingAddress(order.getShippingAddress());
        orderDTO.setPaymentMethod(order.getPaymentMethod());
        orderDTO.setDiscountAmount(order.getDiscountAmount());
        
        // Convert order items
        order.getOrderItems().forEach(orderItem -> {
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setId(orderItem.getId());
            orderItemDTO.setProductId(orderItem.getProduct().getId());
            orderItemDTO.setProductName(orderItem.getProduct().getName());
            orderItemDTO.setProductImageUrl(orderItem.getProduct().getImageUrl());
            orderItemDTO.setQuantity(orderItem.getQuantity());
            orderItemDTO.setPrice(orderItem.getPrice());
            orderDTO.getOrderItems().add(orderItemDTO);
        });
        
        return orderDTO;
    }
}