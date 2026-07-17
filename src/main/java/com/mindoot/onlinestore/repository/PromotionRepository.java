package com.mindoot.onlinestore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.enums.PromotionType;
import com.mindoot.onlinestore.model.promotion.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

	List<Promotion> findByActiveTrue();

	Optional<Promotion> findByCouponCode(String couponCode);

	List<Promotion> findByTypeAndActiveTrue(PromotionType coupon);

}
