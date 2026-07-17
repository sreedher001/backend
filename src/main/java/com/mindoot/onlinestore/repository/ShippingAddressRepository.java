package com.mindoot.onlinestore.repository;

import java.util.List;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.model.ShippingAddress;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

	List<ShippingAddress> findByUserId(Long userId);
    Optional<ShippingAddress> findByIdAndUserId(Long id, Long userId);
}
