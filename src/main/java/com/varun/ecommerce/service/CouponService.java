package com.varun.ecommerce.service;

import java.util.List;

import com.varun.ecommerce.dto.CouponDTO;

public interface CouponService {
    
    CouponDTO createCoupon(CouponDTO couponDTO);
    
    CouponDTO updateCoupon(Long couponId, CouponDTO couponDTO);
    
    CouponDTO getCouponById(Long couponId);
    
    List<CouponDTO> getAllCoupons();
    
    void deleteCoupon(Long couponId);
    
    CouponDTO validateCoupon(String couponCode);
}