package com.mindoot.onlinestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mindoot.onlinestore.model.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

}
