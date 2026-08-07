package com.mindoot.onlinestore.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mindoot.onlinestore.dto.CategoryDto;
import com.mindoot.onlinestore.model.Category;
import com.mindoot.onlinestore.service.CategoryService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/categories")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(toDtoList(categoryService.getAllCategories()));
    }

    @GetMapping("/root")
    public ResponseEntity<List<CategoryDto>> getRootCategories() {
        return ResponseEntity.ok(toDtoList(categoryService.getAllRootCategories()));
    }

    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<CategoryDto>> getSubCategories(@PathVariable("parentId") Long parentId) {
        return ResponseEntity.ok(toDtoList(categoryService.getSubCategories(parentId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(toDto(categoryService.getCategoryById(id)));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryDto> getCategoryBySlug(@PathVariable("slug") String slug) {
        return ResponseEntity.ok(toDto(categoryService.getCategoryBySlug(slug)));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto dto) {
        Category created = categoryService.createCategory(toEntity(dto));
        return ResponseEntity.ok(toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable("id") Long id, @RequestBody CategoryDto dto) {
        Category updated = categoryService.updateCategory(id, toEntity(dto));
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    private List<CategoryDto> toDtoList(List<Category> categories) {
        return categories.stream().map(this::toDto).collect(Collectors.toList());
    }

    private CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImage())
                .sortOrder(category.getSortOrder())
                .active(category.getActive())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }

    private Category toEntity(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        category.setDescription(dto.getDescription());
        category.setImage(dto.getImageUrl());
        category.setSortOrder(dto.getSortOrder());
        category.setActive(dto.getActive());
        if (dto.getParentId() != null) {
            Category parent = new Category();
            parent.setId(dto.getParentId());
            category.setParent(parent);
        }
        return category;
    }
}
