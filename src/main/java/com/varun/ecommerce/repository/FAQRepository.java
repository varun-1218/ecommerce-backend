package com.varun.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.varun.ecommerce.entity.FAQ;
import com.varun.ecommerce.entity.Product;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {
    
    List<FAQ> findByProduct(Product product);
    
    List<FAQ> findByProductId(Long productId);
}