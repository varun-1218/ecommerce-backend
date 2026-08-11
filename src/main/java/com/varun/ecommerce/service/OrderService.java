package com.varun.ecommerce.service;

import java.util.List;

import com.varun.ecommerce.dto.OrderDTO;

public interface OrderService {
    
    OrderDTO placeOrder(Long userId, String shippingAddress, String paymentMethod, String couponCode);
    
    OrderDTO getOrderById(Long orderId);
    
    OrderDTO getOrderByTrackingNumber(String trackingNumber);
    
    List<OrderDTO> getOrdersByUserId(Long userId);
    
    List<OrderDTO> getAllOrders();
    
    OrderDTO updateOrderStatus(Long orderId, String status);
    
    void cancelOrder(Long orderId);
}