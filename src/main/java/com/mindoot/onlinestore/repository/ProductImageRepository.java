package com.mindoot.onlinestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindoot.onlinestore.model.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByVariantId(Long variantId);
}
