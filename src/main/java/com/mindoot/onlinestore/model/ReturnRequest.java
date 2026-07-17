package com.mindoot.onlinestore.model;

import java.time.LocalDateTime;

import com.mindoot.onlinestore.enums.ReturnStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "return_requests",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"order_id", "variant_id"}
       ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who requested
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Order reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Variant being returned
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    private Integer quantity;

    @Column(nullable = false)
    private String reason;

    @Column(length = 1000)
    private String description;
    
    @Column(length = 1000)
    private String adminComment;
    
    @Column(length = 1000)
    private String qcRemarks;

    @Enumerated(EnumType.STRING)
    private ReturnStatus status;

    private LocalDateTime receivedAt;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime closedAt;
    private LocalDateTime refundInitiatedAt;
    private LocalDateTime refundedAt;
    private String refundReference;
}

