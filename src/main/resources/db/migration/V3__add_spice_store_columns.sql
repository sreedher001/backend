-- Spice Store Migration V3
-- Add new columns for spice store product model
-- Uses stored procedure for safe idempotent column additions on MySQL 8.0

DELIMITER //

-- Helper procedure: safely add a column if it doesn't exist
CREATE PROCEDURE add_column_if_not_exists(
    IN p_table VARCHAR(64),
    IN p_column VARCHAR(64),
    IN p_def TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table
          AND COLUMN_NAME = p_column
    ) THEN
        SET @sql = CONCAT('ALTER TABLE ', p_table, ' ADD COLUMN ', p_column, ' ', p_def);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //

-- Helper procedure: safely add an index if it doesn't exist
CREATE PROCEDURE add_index_if_not_exists(
    IN p_table VARCHAR(64),
    IN p_index_name VARCHAR(64),
    IN p_column VARCHAR(64)
)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table
          AND INDEX_NAME = p_index_name
    ) THEN
        SET @sql = CONCAT('CREATE INDEX ', p_index_name, ' ON ', p_table, '(', p_column, ')');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //

DELIMITER ;

-- ===== carts =====
CALL add_column_if_not_exists('carts', 'purchase_type', "VARCHAR(20) DEFAULT 'RETAIL'");

-- ===== cart_items =====
-- Note: variant_id already exists from @JoinColumn on ProductVariant
CALL add_column_if_not_exists('cart_items', 'purchase_type', "VARCHAR(20) DEFAULT 'RETAIL'");
CALL add_column_if_not_exists('cart_items', 'variant_name', 'VARCHAR(255)');
CALL add_column_if_not_exists('cart_items', 'unit_price', 'DOUBLE');
CALL add_column_if_not_exists('cart_items', 'total_price', 'DOUBLE');

-- ===== orders =====
CALL add_column_if_not_exists('orders', 'purchase_type', "VARCHAR(20) DEFAULT 'RETAIL'");
CALL add_column_if_not_exists('orders', 'cgst_amount', 'DOUBLE');
CALL add_column_if_not_exists('orders', 'sgst_amount', 'DOUBLE');

-- ===== order_items =====
CALL add_column_if_not_exists('order_items', 'purchase_type', "VARCHAR(20) DEFAULT 'RETAIL'");
CALL add_column_if_not_exists('order_items', 'variant_name', 'VARCHAR(255)');
CALL add_column_if_not_exists('order_items', 'image_url', 'VARCHAR(500)');

-- ===== users =====
CALL add_column_if_not_exists('users', 'preferred_purchase_type', "VARCHAR(20) DEFAULT 'RETAIL'");

-- ===== products =====
CALL add_column_if_not_exists('products', 'slug', 'VARCHAR(255)');
CALL add_column_if_not_exists('products', 'sku', 'VARCHAR(255)');
CALL add_column_if_not_exists('products', 'barcode', 'VARCHAR(100)');
CALL add_column_if_not_exists('products', 'brand', 'VARCHAR(255)');
CALL add_column_if_not_exists('products', 'short_description', 'VARCHAR(500)');
CALL add_column_if_not_exists('products', 'long_description', 'TEXT');
CALL add_column_if_not_exists('products', 'category_id', 'BIGINT');
CALL add_column_if_not_exists('products', 'thumbnail', 'VARCHAR(500)');
CALL add_column_if_not_exists('products', 'seo_title', 'VARCHAR(200)');
CALL add_column_if_not_exists('products', 'seo_description', 'VARCHAR(500)');
CALL add_column_if_not_exists('products', 'tags', 'VARCHAR(500)');
CALL add_column_if_not_exists('products', 'sort_order', 'INT DEFAULT 0');
CALL add_column_if_not_exists('products', 'active', 'BOOLEAN DEFAULT TRUE');

-- ===== product_variants =====
CALL add_column_if_not_exists('product_variants', 'weight', 'VARCHAR(50)');
CALL add_column_if_not_exists('product_variants', 'unit', 'VARCHAR(20)');
CALL add_column_if_not_exists('product_variants', 'sku', 'VARCHAR(255)');
CALL add_column_if_not_exists('product_variants', 'barcode', 'VARCHAR(100)');
CALL add_column_if_not_exists('product_variants', 'retail_price', 'DOUBLE');
CALL add_column_if_not_exists('product_variants', 'wholesale_price', 'DOUBLE');
CALL add_column_if_not_exists('product_variants', 'wholesale_enabled', 'BOOLEAN DEFAULT FALSE');
CALL add_column_if_not_exists('product_variants', 'min_wholesale_quantity', 'INT');
CALL add_column_if_not_exists('product_variants', 'wholesale_discount', 'DOUBLE');
CALL add_column_if_not_exists('product_variants', 'active', 'BOOLEAN DEFAULT TRUE');
CALL add_column_if_not_exists('product_variants', 'sort_order', 'INT DEFAULT 0');
CALL add_column_if_not_exists('product_variants', 'image_url', 'VARCHAR(500)');

-- ===== product_images =====
CALL add_column_if_not_exists('product_images', 'is_thumbnail', 'BOOLEAN DEFAULT FALSE');
CALL add_column_if_not_exists('product_images', 'sort_order', 'INT DEFAULT 0');
CALL add_column_if_not_exists('product_images', 'product_id', 'BIGINT');

-- ===== shipping_rules =====
CALL add_column_if_not_exists('shipping_rules', 'purchase_type', "VARCHAR(20) DEFAULT 'RETAIL'");
CALL add_column_if_not_exists('shipping_rules', 'local_pickup', 'BOOLEAN DEFAULT FALSE');
CALL add_column_if_not_exists('shipping_rules', 'pickup_address', 'VARCHAR(500)');

-- ===== Indexes =====
CALL add_index_if_not_exists('products', 'idx_products_active', 'active');
CALL add_index_if_not_exists('products', 'idx_products_featured', 'isFeatured');
CALL add_index_if_not_exists('products', 'idx_products_category', 'category_id');
CALL add_index_if_not_exists('product_variants', 'idx_product_variants_product', 'product_id');
CALL add_index_if_not_exists('product_variants', 'idx_product_variants_active', 'active');
CALL add_index_if_not_exists('orders', 'idx_orders_purchase_type', 'purchase_type');
CALL add_index_if_not_exists('cart_items', 'idx_cart_items_purchase_type', 'purchase_type');

-- Drop helper procedures
DROP PROCEDURE IF EXISTS add_column_if_not_exists;
DROP PROCEDURE IF EXISTS add_index_if_not_exists;
