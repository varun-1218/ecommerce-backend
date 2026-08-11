//package com.varun.ecommerce;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class EcommerceApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(EcommerceApplication.class, args);
//	}
//
//}





package com.varun.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class EcommerceApplication {
    
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EcommerceApplication.class, args);
        
        // Print all beans to check if controllers are created
        System.out.println("========================================");
        System.out.println("=== CHECKING BEANS ===");
        System.out.println("AuthController: " + context.containsBean("authController"));
        System.out.println("TestController: " + context.containsBean("testController"));
        System.out.println("UserService: " + context.containsBean("userServiceImpl"));
        System.out.println("JwtService: " + context.containsBean("jwtService"));
        System.out.println("========================================");
        
        // Print all controller names
        System.out.println("=== CONTROLLERS ===");
        String[] beanNames = context.getBeanNamesForType(Object.class);
        for (String name : beanNames) {
            if (name.contains("Controller") || name.contains("controller")) {
                System.out.println("  - " + name);
            }
        }
        System.out.println("========================================");
    }
}