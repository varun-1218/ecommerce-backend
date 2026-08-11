package com.varun.ecommerce.service;

import java.util.List;

import com.varun.ecommerce.dto.ProductDTO;

public interface ProductService {
    
    ProductDTO createProduct(ProductDTO productDTO);
    
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    
    ProductDTO getProductById(Long productId);
    
    List<ProductDTO> getAllProducts();
    
    List<ProductDTO> getProductsByCategory(Long categoryId);
    
    List<ProductDTO> searchProductsByName(String name);
    
    void deleteProduct(Long productId);
    
    ProductDTO updateStockQuantity(Long productId, Integer quantity);
}
