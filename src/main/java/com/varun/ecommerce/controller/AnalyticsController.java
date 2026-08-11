package com.varun.ecommerce.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.varun.ecommerce.entity.Order;
import com.varun.ecommerce.repository.OrderRepository;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @GetMapping("/earnings")
    public ResponseEntity<Map<String, Object>> getEarningsAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();
        
        List<Order> orders = orderRepository.findAll().stream()
            .filter(order -> order.getOrderDate() != null)
            .filter(order -> !order.getOrderDate().isBefore(startDateTime) && !order.getOrderDate().isAfter(endDateTime))
            .toList();
        
        BigDecimal totalEarnings = orders.stream()
            .filter(order -> "DELIVERED".equals(order.getStatus()))
            .map(Order::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalOrders = orders.size();
        long pendingOrders = orders.stream().filter(order -> "PENDING".equals(order.getStatus())).count();
        long deliveredOrders = orders.stream().filter(order -> "DELIVERED".equals(order.getStatus())).count();
        long cancelledOrders = orders.stream().filter(order -> "CANCELLED".equals(order.getStatus())).count();
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalEarnings", totalEarnings);
        analytics.put("totalOrders", totalOrders);
        analytics.put("pendingOrders", pendingOrders);
        analytics.put("deliveredOrders", deliveredOrders);
        analytics.put("cancelledOrders", cancelledOrders);
        analytics.put("period", Map.of("start", startDateTime, "end", endDateTime));
        
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/daily-earnings")
    public ResponseEntity<Map<String, BigDecimal>> getDailyEarnings(
            @RequestParam(defaultValue = "7") int days) {
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        Map<String, BigDecimal> dailyEarnings = new HashMap<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            
            List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> order.getOrderDate() != null)
                .filter(order -> !order.getOrderDate().isBefore(startOfDay) && !order.getOrderDate().isAfter(endOfDay))
                .filter(order -> "DELIVERED".equals(order.getStatus()))
                .toList();
            
            BigDecimal dayEarnings = orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            dailyEarnings.put(date.toString(), dayEarnings);
        }
        
        return ResponseEntity.ok(dailyEarnings);
    }
    
    @GetMapping("/top-products")
    public ResponseEntity<Map<String, Object>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {
        
        // This would require a more complex query to get top-selling products
        // For now, we'll return a simplified version
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Top products analytics endpoint");
        response.put("limit", limit);
        
        // In real implementation, you would query order items and group by product
        
        return ResponseEntity.ok(response);
    }
}