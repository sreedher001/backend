package com.mindoot.onlinestore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindoot.onlinestore.enums.PurchaseType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id")
	@JsonIgnore
	private Cart cart;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "variant_id")
	@ToString.Exclude
	@JsonIgnore
	private ProductVariant variant;

	private String variantName;

	private Integer quantity;

	private Double unitPrice;

	private Double totalPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private PurchaseType purchaseType = PurchaseType.RETAIL;
}
