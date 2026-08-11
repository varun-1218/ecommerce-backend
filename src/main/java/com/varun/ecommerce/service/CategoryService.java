package com.varun.ecommerce.service;

import java.util.List;

import com.varun.ecommerce.dto.CategoryDTO;

public interface CategoryService {
    
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    
    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);
    
    CategoryDTO getCategoryById(Long categoryId);
    
    List<CategoryDTO> getAllCategories();
    
    void deleteCategory(Long categoryId);
}
