package com.varun.ecommerce.service;

import com.varun.ecommerce.dto.AuthRequest;
import com.varun.ecommerce.dto.AuthResponse;
import com.varun.ecommerce.dto.UserDTO;

public interface UserService {
    
    UserDTO registerUser(AuthRequest authRequest);
    
    AuthResponse loginUser(AuthRequest authRequest);
    
    UserDTO getUserById(Long userId);
    
    UserDTO getUserByEmail(String email);
    
    UserDTO updateUser(UserDTO userDTO);
    
    void deleteUser(Long userId);
}