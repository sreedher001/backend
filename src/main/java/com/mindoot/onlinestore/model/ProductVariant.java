package com.mindoot.onlinestore.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@JsonIgnore
	private Product product;

	private String weight;

	private String unit;

	private String variantName;

	@Column(nullable = false, unique = true)
	private String sku;

	private String barcode;

	/** SEO-friendly URL segment, e.g. "turmeric-powder-100g". Generated from
	 * the parent product's slug plus weight/unit at creation time. */
	@Column(unique = true)
	private String slug;

	private Double retailPrice;

	/** Original "before discount" price. When set higher than retailPrice, the
	 * storefront shows it struck through next to the current retail price. */
	private Double mrp;

	private Double wholesalePrice;

	/** Whether this variant is sold to retail customers. Together with
	 * wholesaleEnabled this forms a Retail-only / Wholesale-only / Both
	 * availability toggle, editable from a single admin screen. */
	private Boolean retailEnabled = true;

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
