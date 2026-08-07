package com.mindoot.onlinestore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Single-row table holding all tenant/store-identity details (name, contact,
 * branding, SEO defaults). Kept as one editable record so this codebase can
 * be resold and re-branded per customer entirely through the admin panel,
 * with no source changes required.
 */
@Entity
@Table(name = "store_settings")
@Getter
@Setter
public class StoreSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;
    private String tagline;
    private String aboutDescription;
    private String logoUrl;
    private String faviconUrl;

    private String supportEmail;
    private String supportPhone;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    private String instagramUrl;
    private String facebookUrl;
    private String twitterUrl;
    private String linkedinUrl;

    private String websiteDomain;
    private String seoTitle;
    private String seoDescription;

    private String currencySymbol;

    /** Hex color (e.g. "#c2410c") used as the PrimeNG theme's primary brand color. */
    private String primaryColor;
}
