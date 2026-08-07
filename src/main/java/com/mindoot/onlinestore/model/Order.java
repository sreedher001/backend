package com.mindoot.onlinestore.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindoot.onlinestore.enums.OrderStatus;
import com.mindoot.onlinestore.enums.PurchaseType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    private LocalDateTime orderDate;

    private String paymentMode;

    private String paymentId;

    private LocalDateTime paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    private ShippingAddress shippingAddress;

    private Double totalAmount;

    private Double subtotal;

    private Double shippingFee;

    private Double discountAmount;

    private Double shippingDiscount;

    private Double cgstAmount;

    private Double sgstAmount;

    private String appliedCouponCode;

    private Long appliedPromotionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private OrderStatus status = OrderStatus.PLACED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PurchaseType purchaseType = PurchaseType.RETAIL;

    private boolean cancelledByAdmin;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private LocalDateTime updatedAt;
}
