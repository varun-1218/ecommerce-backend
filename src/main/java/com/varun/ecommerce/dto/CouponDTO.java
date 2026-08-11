package com.varun.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CouponDTO {
    
    private Long id;
    private String code;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private LocalDate expirationDate;
    private Integer usageLimit;
    private Integer usedCount;
    
    public CouponDTO() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public Integer getUsageLimit() {
        return usageLimit;
    }
    
    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }
    
    public Integer getUsedCount() {
        return usedCount;
    }
    
    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }
}
