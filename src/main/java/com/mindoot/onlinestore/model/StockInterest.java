package com.mindoot.onlinestore.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock_interest",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"variant_id", "user_id"}),
           @UniqueConstraint(columnNames = {"variant_id", "email"})
       })
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "variant_id", nullable = false)
    private Long variantId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email")
    private String email;

    private boolean notified = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
