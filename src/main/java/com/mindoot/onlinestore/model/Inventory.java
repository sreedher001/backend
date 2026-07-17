package com.mindoot.onlinestore.model;

import java.time.LocalDateTime;

import com.mindoot.onlinestore.enums.InventoryStatus;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "variant_id", nullable = false, unique = true)
	private ProductVariant variant;

	private Integer availableQuantity;
	private Integer reservedQuantity;
	private Integer lowStockThreshold;

	@Enumerated(EnumType.STRING)
	private InventoryStatus inventoryStatus;

	private LocalDateTime lastUpdated;
}
