package com.mindoot.onlinestore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindoot.onlinestore.enums.PurchaseType;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, unique = true)
    @JsonIgnore
    private User user;

    @Column(name = "guest_id", unique = true)
    private String guestId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String appliedCoupon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PurchaseType purchaseType = PurchaseType.RETAIL;
}
