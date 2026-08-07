package com.mindoot.onlinestore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "product_search_index",
    indexes = {
        @Index(name="idx_search_category", columnList="categoryId"),
        @Index(name="idx_search_brand", columnList="brand"),
        @Index(name="idx_search_price", columnList="retailPrice"),
        @Index(name="idx_search_rating", columnList="rating"),
        @Index(name="idx_search_featured", columnList="isFeatured"),
        @Index(name="idx_search_active", columnList="active"),
        @Index(name="idx_search_category_price", columnList="categoryId,retailPrice")
    }
)
@Getter
@Setter
public class ProductSearchIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long variantId;

    private String productName;
    private String variantName;
    private String slug;
    private String variantSlug;

    private Long categoryId;
    private String categoryName;
    private Long subCategoryId;
    private String subCategoryName;

    private String brand;
    private String tags;

    private String weight;
    private String unit;

    private Double retailPrice;
    private Double mrp;
    private Double wholesalePrice;
    private Double discountPercentage;
    private Boolean retailEnabled;
    private Boolean wholesaleEnabled;

    private Double rating;

    private Boolean isFeatured;
    private Boolean active;

    private String imageUrl;

    private Boolean inStock;

    private String sku;
}
