package com.mindoot.onlinestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.ProductCode;

@Repository
public interface ProductCodeRepository extends JpaRepository<ProductCode, Long>{

}
