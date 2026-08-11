package com.varun.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.varun.ecommerce.entity.Cart;
import com.varun.ecommerce.entity.User;
import com.varun.ecommerce.repository.CartRepository;
import com.varun.ecommerce.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create admin user if not exists
        if (!userRepository.existsByEmail("admin@example.com")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN"); // CHANGE: Use String "ADMIN"
            
            User savedAdmin = userRepository.save(admin);
            
            // Create cart for admin
            Cart cart = new Cart(savedAdmin);
            cartRepository.save(cart);
            
            System.out.println("Admin user created: admin@example.com / admin123");
        }
        
        // Create regular user if not exists
        if (!userRepository.existsByEmail("user@example.com")) {
            User user = new User();
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("USER"); // CHANGE: Use String "USER"
            
            User savedUser = userRepository.save(user);
            
            // Create cart for user
            Cart cart = new Cart(savedUser);
            cartRepository.save(cart);
            
            System.out.println("Test user created: user@example.com / user123");
        }
    }
}