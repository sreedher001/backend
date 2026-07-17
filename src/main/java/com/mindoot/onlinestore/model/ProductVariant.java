package com.mindoot.onlinestore.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variants", indexes = {
    @Index(name = "idx_variant_sku", columnList = "sku", unique = true),
    @Index(name = "idx_variant_barcode", columnList = "barcode"),
    @Index(name = "idx_variant_product", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	private String weight;

	private String unit;

	private String variantName;

	@Column(nullable = false, unique = true)
	private String sku;

	private String barcode;

	private Double retailPrice;

	private Double wholesalePrice;

	private Boolean wholesaleEnabled = false;

	private Integer minWholesaleQuantity;

	private Double wholesaleDiscount;

	private Boolean active = true;

	private Integer sortOrder = 0;

	private String imageUrl;

	private Double rating;

	private Boolean isFeatured = false;

	@OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ProductImage> images = new ArrayList<>();

	@OneToOne(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
	private Inventory inventory;
}
