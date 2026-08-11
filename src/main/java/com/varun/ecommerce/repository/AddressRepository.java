package com.varun.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.varun.ecommerce.entity.Address;
import com.varun.ecommerce.entity.User;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByUser(User user);
    
    List<Address> findByUserId(Long userId);
    
    Address findByUserIdAndIsDefaultTrue(Long userId);
}