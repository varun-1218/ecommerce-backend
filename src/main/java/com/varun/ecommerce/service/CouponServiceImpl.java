package com.varun.ecommerce.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.varun.ecommerce.dto.CouponDTO;
import com.varun.ecommerce.entity.Coupon;
import com.varun.ecommerce.repository.CouponRepository;

@Service
public class CouponServiceImpl implements CouponService {
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Override
    public CouponDTO createCoupon(CouponDTO couponDTO) {
        // Check if coupon code already exists
        if (couponRepository.existsByCode(couponDTO.getCode())) {
            throw new RuntimeException("Coupon code already exists");
        }
        
        Coupon coupon = new Coupon();
        coupon.setCode(couponDTO.getCode());
        coupon.setDiscountPercentage(couponDTO.getDiscountPercentage());
        coupon.setDiscountAmount(couponDTO.getDiscountAmount());
        coupon.setExpirationDate(couponDTO.getExpirationDate());
        coupon.setUsageLimit(couponDTO.getUsageLimit());
        coupon.setUsedCount(0);
        
        Coupon savedCoupon = couponRepository.save(coupon);
        return convertToDTO(savedCoupon);
    }
    
    @Override
    public CouponDTO updateCoupon(Long couponId, CouponDTO couponDTO) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        coupon.setDiscountPercentage(couponDTO.getDiscountPercentage());
        coupon.setDiscountAmount(couponDTO.getDiscountAmount());
        coupon.setExpirationDate(couponDTO.getExpirationDate());
        coupon.setUsageLimit(couponDTO.getUsageLimit());
        
        Coupon updatedCoupon = couponRepository.save(coupon);
        return convertToDTO(updatedCoupon);
    }
    
    @Override
    public CouponDTO getCouponById(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
        return convertToDTO(coupon);
    }
    
    @Override
    public List<CouponDTO> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        return coupons.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
        couponRepository.delete(coupon);
    }
    
    @Override
    public CouponDTO validateCoupon(String couponCode) {
        Coupon coupon = couponRepository.findByCode(couponCode)
            .orElseThrow(() -> new RuntimeException("Invalid coupon code"));
        
        // Check expiration
        if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Coupon has expired");
        }
        
        // Check usage limit
        if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new RuntimeException("Coupon usage limit reached");
        }
        
        return convertToDTO(coupon);
    }
    
    private CouponDTO convertToDTO(Coupon coupon) {
        CouponDTO couponDTO = new CouponDTO();
        couponDTO.setId(coupon.getId());
        couponDTO.setCode(coupon.getCode());
        couponDTO.setDiscountPercentage(coupon.getDiscountPercentage());
        couponDTO.setDiscountAmount(coupon.getDiscountAmount());
        couponDTO.setExpirationDate(coupon.getExpirationDate());
        couponDTO.setUsageLimit(coupon.getUsageLimit());
        couponDTO.setUsedCount(coupon.getUsedCount());
        return couponDTO;
    }
}