package com.mindoot.onlinestore.model;

import com.mindoot.onlinestore.enums.PurchaseType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipping_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double minCartValue;

    private Double maxCartValue;

    private Double shippingFee;

    private Boolean freeShipping;

    private Integer priority;

    private Boolean active;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PurchaseType purchaseType = PurchaseType.RETAIL;

    private String pickupAddress;

    private Boolean localPickup = false;
}
