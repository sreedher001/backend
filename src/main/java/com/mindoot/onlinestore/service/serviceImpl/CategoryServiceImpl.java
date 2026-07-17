package com.mindoot.onlinestore.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindoot.onlinestore.exception.BadRequestException;
import com.mindoot.onlinestore.model.Category;
import com.mindoot.onlinestore.repository.CategoryRepository;
import com.mindoot.onlinestore.service.CategoryService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new BadRequestException("Category with name '" + category.getName() + "' already exists");
        }
        if (category.getSlug() == null || category.getSlug().isEmpty()) {
            category.setSlug(generateSlug(category.getName()));
        }
        if (categoryRepository.existsBySlug(category.getSlug())) {
            throw new BadRequestException("Category with slug '" + category.getSlug() + "' already exists");
        }
        if (category.getParent() != null && category.getParent().getId() != null) {
            Optional<Category> parent = categoryRepository.findById(category.getParent().getId());
            if (parent.isEmpty()) {
                throw new BadRequestException("Parent category not found");
            }
            category.setParent(parent.get());
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Category category) {
        Category existing = categoryRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Category not found"));
        existing.setName(category.getName());
        if (category.getSlug() != null) {
            existing.setSlug(category.getSlug());
        }
        existing.setDescription(category.getDescription());
        existing.setImage(category.getImage());
        existing.setSortOrder(category.getSortOrder());
        existing.setActive(category.getActive());
        if (category.getParent() != null && category.getParent().getId() != null) {
            Optional<Category> parent = categoryRepository.findById(category.getParent().getId());
            parent.ifPresent(existing::setParent);
        } else {
            existing.setParent(null);
        }
        return categoryRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Category not found"));
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            throw new BadRequestException("Cannot delete category with subcategories. Remove subcategories first.");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Category not found"));
    }

    @Override
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
            .orElseThrow(() -> new BadRequestException("Category not found"));
    }

    @Override
    public List<Category> getAllRootCategories() {
        return categoryRepository.findByParentIsNullAndActiveTrueOrderBySortOrderAsc();
    }

    @Override
    public List<Category> getSubCategories(Long parentId) {
        return categoryRepository.findByParentIdAndActiveTrueOrderBySortOrderAsc(parentId);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc();
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return categoryRepository.existsBySlug(slug);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
    }
}
