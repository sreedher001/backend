package com.mindoot.onlinestore.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.enums.OrderStatus;
import com.mindoot.onlinestore.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByOrderNumberAndUserId(String orderNumber, Long userId);

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    Page<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Order> findByOrderDateBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, OrderStatus status, Pageable pageable);

    Page<Order> findByOrderDateGreaterThanEqualAndOrderDateLessThan(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.paymentId = :paymentId")
    Optional<Order> findByPaymentId(@Param("paymentId") String paymentId);

    long countByOrderDateBetweenAndPurchaseType(LocalDateTime start, LocalDateTime end, String purchaseType);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderDate BETWEEN :start AND :end AND o.status IN ('PLACED','CONFIRMED','PACKED','SHIPPED','DELIVERED') AND o.purchaseType = :purchaseType")
    Double sumSalesByDateRangeAndPurchaseType(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("purchaseType") String purchaseType);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderDate BETWEEN :start AND :end AND o.status IN ('PLACED','CONFIRMED','PACKED','SHIPPED','DELIVERED')")
    Double sumTotalSalesByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :start AND :end AND o.status IN ('PLACED','CONFIRMED','PACKED','SHIPPED','DELIVERED')")
    long countOrdersByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN ('PLACED','CONFIRMED','PACKED','SHIPPED','DELIVERED')")
    long countAllActiveOrders();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status IN ('PLACED','CONFIRMED','PACKED','SHIPPED','DELIVERED')")
    Double sumAllActiveSales();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.purchaseType = 'RETAIL' AND o.status IN ('PLACED','CONFIRMED','PACKED','SHIPPED','DELIVERED')")
    Double sumRetailSales();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.purchaseType = 'WHOLESALE' AND o.status IN ('PLACED','CONFIRMED','PACKED','SHIPPED','DELIVERED')")
    Double sumWholesaleSales();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.purchaseType = 'RETAIL' AND o.status IN ('PLACED','CONFIRMED','PACKED','SHIPPED','DELIVERED')")
    long countRetailOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.purchaseType = 'WHOLESALE' AND o.status IN ('PLACED','CONFIRMED','PACKED','SHIPPED','DELIVERED')")
    long countWholesaleOrders();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items LEFT JOIN FETCH o.user WHERE o.id = :id")
    Optional<Order> findByIdWithItemsAndUser(@Param("id") Long id);

    @Query("SELECT o FROM Order o WHERE o.orderDate >= :since AND o.status IN ('PLACED','CONFIRMED','PACKED','SHIPPED','DELIVERED','PAID')")
    List<Order> findOrdersSince(@Param("since") LocalDateTime since);

    boolean existsByUserId(Long userId);
}
