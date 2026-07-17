package com.mindoot.onlinestore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mindoot.onlinestore.enums.ReturnStatus;
import com.mindoot.onlinestore.model.Order;
import com.mindoot.onlinestore.model.ProductVariant;
import com.mindoot.onlinestore.model.ReturnRequest;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    boolean existsByOrderAndVariant(Order order, ProductVariant variant);

    List<ReturnRequest> findByStatus(ReturnStatus status);

    List<ReturnRequest> findByUserId(Long userId);
    Page<ReturnRequest> findAllByStatus(
            ReturnStatus status,
            Pageable pageable
        );
}