-- ============================================================
--  V2__add_fulltext_indexes.sql
--  Safe migration for MySQL 8 (works in Flyway)
-- ============================================================

-- Drop existing indexes if they exist
SET @idx_color_exists := (
  SELECT COUNT(*) 
  FROM information_schema.statistics 
  WHERE table_schema = DATABASE() 
    AND table_name = 'product_variants' 
    AND index_name = 'color'
);
SET @drop_color_sql := IF(@idx_color_exists > 0, 'DROP INDEX color ON product_variants;', 'SELECT "no color index";');
PREPARE stmt FROM @drop_color_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_name_exists := (
  SELECT COUNT(*) 
  FROM information_schema.statistics 
  WHERE table_schema = DATABASE() 
    AND table_name = 'products' 
    AND index_name = 'name'
);
SET @drop_name_sql := IF(@idx_name_exists > 0, 'DROP INDEX name ON products;', 'SELECT "no name index";');
PREPARE stmt2 FROM @drop_name_sql;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

-- Add proper FULLTEXT indexes
ALTER TABLE product_variants 
ADD FULLTEXT INDEX idx_product_variants_fulltext (
    variant_name,
    color,
    fit,
    pattern,
    style_category,
    season,
    occasion,
    description
);

ALTER TABLE products 
ADD FULLTEXT INDEX idx_products_fulltext (
    name,
    category,
    sub_category,
    gender_category,
    description
);
