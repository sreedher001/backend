package com.mindoot.onlinestore.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.model.Category;

@Component
public interface CategoryService {

    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    void deleteCategory(Long id);

    Category getCategoryById(Long id);

    Category getCategoryBySlug(String slug);

    List<Category> getAllRootCategories();

    List<Category> getSubCategories(Long parentId);

    List<Category> getAllCategories();

    boolean existsByName(String name);

    boolean existsBySlug(String slug);
}
