package com.varun.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.varun.ecommerce.config.JwtService;
import com.varun.ecommerce.dto.AuthRequest;
import com.varun.ecommerce.dto.AuthResponse;
import com.varun.ecommerce.dto.UserDTO;
import com.varun.ecommerce.entity.Cart;
import com.varun.ecommerce.entity.User;
import com.varun.ecommerce.repository.CartRepository;
import com.varun.ecommerce.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    // NO AuthenticationManager - we'll handle manually
    
    @Override
    public UserDTO registerUser(AuthRequest authRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(authRequest.getEmail())) {
            throw new RuntimeException("User already exists with email: " + authRequest.getEmail());
        }
        
        // Create new user
        User user = new User();
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole("USER");  // Use String "USER"
        
        User savedUser = userRepository.save(user);
        
        // Create cart for the user
        Cart cart = new Cart(savedUser);
        cartRepository.save(cart);
        
        return convertToDTO(savedUser);
    }
    
    @Override
    public AuthResponse loginUser(AuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check password manually
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        String jwtToken = jwtService.generateToken(user.getEmail());
        UserDTO userDTO = convertToDTO(user);
        
        return new AuthResponse(jwtToken, "Login successful", userDTO);
    }
    
    @Override
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return convertToDTO(user);
    }
    
    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return convertToDTO(user);
    }
    
    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    
    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());  // Already a String
        return userDTO;
    }
}