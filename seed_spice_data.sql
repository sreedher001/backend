USE spice_store;

-- ===== Categories =====
INSERT INTO categories (id, name, slug, description, parent_id, active, sort_order, image) VALUES
(1, 'Spices & Masalas', 'spices-masalas', 'Whole and ground spices, curry powders, and specialty masala blends.', NULL, b'1', 1, 'https://picsum.photos/seed/spices-masalas/600/400'),
(2, 'Tea & Coffee', 'tea-coffee', 'Premium tea powders, green tea, and freshly ground coffee.', NULL, b'1', 2, 'https://picsum.photos/seed/tea-coffee/600/400'),
(3, 'Dry Fruits & Nuts', 'dry-fruits-nuts', 'Almonds, cashews, raisins, and more in various pack sizes.', NULL, b'1', 3, 'https://picsum.photos/seed/dry-fruits-nuts/600/400'),
(4, 'Herbs & Seasonings', 'herbs-seasonings', 'Basil, oregano, turmeric, and everyday kitchen essentials.', NULL, b'1', 4, 'https://picsum.photos/seed/herbs-seasonings/600/400');

-- ===== Products =====
INSERT INTO products (id, product_id, name, slug, short_description, long_description, category_id, sub_category_id, brand, sku, barcode, active, is_featured, thumbnail, seo_title, seo_description, tags, sort_order, rating, uploaded_by, uploaded_at) VALUES
(1, 'PRD-001', 'Turmeric Powder', 'turmeric-powder', 'Pure, vibrant turmeric powder sourced from Erode farms.', 'Our turmeric powder is stone-ground from sun-dried turmeric fingers, retaining its natural aroma, colour and curcumin content. Free from additives and artificial colouring.', 1, NULL, 'Spice Store', 'SPC-TUR', 'SPC-TUR-BC', b'1', b'1', 'https://picsum.photos/seed/turmeric/500/500', 'Turmeric Powder | Spice Store', 'Buy pure turmeric powder online, retail and wholesale.', 'turmeric,masala,spice', 1, 4.5, NULL, NOW()),
(2, 'PRD-002', 'Kashmiri Red Chilli Powder', 'kashmiri-red-chilli-powder', 'Mild heat, deep red colour, perfect for gravies.', 'Sourced from Kashmiri chillies known for their rich colour and mild pungency. Ideal for tandoori marinades and curries that need colour without excess heat.', 1, NULL, 'Spice Store', 'SPC-KRC', 'SPC-KRC-BC', b'1', b'1', 'https://picsum.photos/seed/kashmiri-chilli/500/500', 'Kashmiri Red Chilli Powder | Spice Store', 'Buy Kashmiri red chilli powder online, retail and wholesale.', 'chilli,masala,spice', 2, 4.3, NULL, NOW()),
(3, 'PRD-003', 'Assam Black Tea', 'assam-black-tea', 'Bold, malty black tea leaves from Assam gardens.', 'Hand-picked from the tea gardens of Assam, this full-bodied black tea delivers a rich malty flavour, perfect for a strong morning cup.', 2, NULL, 'Spice Store', 'TEA-ASM', 'TEA-ASM-BC', b'1', b'0', 'https://picsum.photos/seed/assam-tea/500/500', 'Assam Black Tea | Spice Store', 'Buy Assam black tea online, retail and wholesale.', 'tea,assam,beverage', 1, 4.6, NULL, NOW()),
(4, 'PRD-004', 'Filter Coffee Powder', 'filter-coffee-powder', 'Traditional South Indian filter coffee blend.', 'A classic blend of arabica and chicory roasted and ground fresh for that authentic South Indian filter coffee experience.', 2, NULL, 'Spice Store', 'COF-FLT', 'COF-FLT-BC', b'1', b'1', 'https://picsum.photos/seed/filter-coffee/500/500', 'Filter Coffee Powder | Spice Store', 'Buy filter coffee powder online, retail and wholesale.', 'coffee,beverage', 2, 4.7, NULL, NOW()),
(5, 'PRD-005', 'Premium Cashews', 'premium-cashews', 'Whole, crunchy W240 grade cashew nuts.', 'Sourced from the coastal cashew belts, these whole cashews are lightly processed to retain natural crunch and flavour. Great for snacking and gifting.', 3, NULL, 'Spice Store', 'DFN-CSH', 'DFN-CSH-BC', b'1', b'1', 'https://picsum.photos/seed/cashews/500/500', 'Premium Cashews | Spice Store', 'Buy premium cashews online, retail and wholesale.', 'cashew,dryfruit,nuts', 1, 4.8, NULL, NOW()),
(6, 'PRD-006', 'California Almonds', 'california-almonds', 'Crunchy, protein-rich California almonds.', 'Premium quality almonds imported from California, rich in protein and healthy fats. Perfect for daily snacking or festive gifting.', 3, NULL, 'Spice Store', 'DFN-ALM', 'DFN-ALM-BC', b'1', b'0', 'https://picsum.photos/seed/almonds/500/500', 'California Almonds | Spice Store', 'Buy California almonds online, retail and wholesale.', 'almond,dryfruit,nuts', 2, 4.4, NULL, NOW()),
(7, 'PRD-007', 'Dried Basil Leaves', 'dried-basil-leaves', 'Aromatic dried basil for pastas and seasoning.', 'Sun-dried basil leaves that retain their aromatic oils, perfect for Italian dishes, soups and everyday seasoning.', 4, NULL, 'Spice Store', 'HRB-BSL', 'HRB-BSL-BC', b'1', b'0', 'https://picsum.photos/seed/basil/500/500', 'Dried Basil Leaves | Spice Store', 'Buy dried basil leaves online, retail and wholesale.', 'basil,herb,seasoning', 1, 4.2, NULL, NOW()),
(8, 'PRD-008', 'Curry Leaves Powder', 'curry-leaves-powder', 'Fragrant curry leaves powder for tempering.', 'Made from shade-dried curry leaves, this powder adds authentic South Indian aroma to rice, dals and curries.', 4, NULL, 'Spice Store', 'HRB-CUR', 'HRB-CUR-BC', b'1', b'0', 'https://picsum.photos/seed/curryleaves/500/500', 'Curry Leaves Powder | Spice Store', 'Buy curry leaves powder online, retail and wholesale.', 'curryleaves,herb,seasoning', 2, 4.1, NULL, NOW());

-- ===== Product Variants =====
INSERT INTO product_variants (id, product_id, variant_name, weight, unit, sku, barcode, retail_price, wholesale_price, wholesale_enabled, min_wholesale_quantity, wholesale_discount, active, sort_order, image_url, is_featured, rating) VALUES
(1, 1, 'Turmeric Powder 100g', '100', 'g', 'SPC-TUR-100', 'SPC-TUR-100-BC', 60, 45, b'1', 10, 25, b'1', 1, 'https://picsum.photos/seed/turmeric-100/500/500', b'1', 4.5),
(2, 1, 'Turmeric Powder 500g', '500', 'g', 'SPC-TUR-500', 'SPC-TUR-500-BC', 250, 190, b'1', 10, 24, b'1', 2, 'https://picsum.photos/seed/turmeric-500/500/500', b'0', 4.5),
(3, 2, 'Kashmiri Red Chilli 100g', '100', 'g', 'SPC-KRC-100', 'SPC-KRC-100-BC', 90, 70, b'1', 10, 22, b'1', 1, 'https://picsum.photos/seed/kashmiri-chilli-100/500/500', b'1', 4.3),
(4, 2, 'Kashmiri Red Chilli 500g', '500', 'g', 'SPC-KRC-500', 'SPC-KRC-500-BC', 400, 320, b'1', 10, 20, b'1', 2, 'https://picsum.photos/seed/kashmiri-chilli-500/500/500', b'0', 4.3),
(5, 3, 'Assam Black Tea 250g', '250', 'g', 'TEA-ASM-250', 'TEA-ASM-250-BC', 180, 140, b'1', 5, 22, b'1', 1, 'https://picsum.photos/seed/assam-tea-250/500/500', b'1', 4.6),
(6, 3, 'Assam Black Tea 500g', '500', 'g', 'TEA-ASM-500', 'TEA-ASM-500-BC', 340, 270, b'1', 5, 20, b'1', 2, 'https://picsum.photos/seed/assam-tea-500/500/500', b'0', 4.6),
(7, 4, 'Filter Coffee Powder 200g', '200', 'g', 'COF-FLT-200', 'COF-FLT-200-BC', 220, 170, b'1', 5, 22, b'1', 1, 'https://picsum.photos/seed/filter-coffee-200/500/500', b'1', 4.7),
(8, 4, 'Filter Coffee Powder 500g', '500', 'g', 'COF-FLT-500', 'COF-FLT-500-BC', 500, 400, b'1', 5, 20, b'1', 2, 'https://picsum.photos/seed/filter-coffee-500/500/500', b'0', 4.7),
(9, 5, 'Premium Cashews 250g', '250', 'g', 'DFN-CSH-250', 'DFN-CSH-250-BC', 350, 280, b'1', 5, 20, b'1', 1, 'https://picsum.photos/seed/cashews-250/500/500', b'1', 4.8),
(10, 5, 'Premium Cashews 1kg', '1', 'kg', 'DFN-CSH-1000', 'DFN-CSH-1000-BC', 1300, 1050, b'1', 5, 19, b'1', 2, 'https://picsum.photos/seed/cashews-1000/500/500', b'0', 4.8),
(11, 6, 'California Almonds 250g', '250', 'g', 'DFN-ALM-250', 'DFN-ALM-250-BC', 300, 240, b'1', 5, 20, b'1', 1, 'https://picsum.photos/seed/almonds-250/500/500', b'1', 4.4),
(12, 6, 'California Almonds 1kg', '1', 'kg', 'DFN-ALM-1000', 'DFN-ALM-1000-BC', 1100, 900, b'1', 5, 18, b'1', 2, 'https://picsum.photos/seed/almonds-1000/500/500', b'0', 4.4),
(13, 7, 'Dried Basil Leaves 50g', '50', 'g', 'HRB-BSL-50', 'HRB-BSL-50-BC', 80, 60, b'1', 10, 25, b'1', 1, 'https://picsum.photos/seed/basil-50/500/500', b'0', 4.2),
(14, 8, 'Curry Leaves Powder 100g', '100', 'g', 'HRB-CUR-100', 'HRB-CUR-100-BC', 70, 55, b'1', 10, 21, b'1', 1, 'https://picsum.photos/seed/curryleaves-100/500/500', b'0', 4.1);

-- ===== Inventory =====
INSERT INTO inventory (variant_id, available_quantity, reserved_quantity, low_stock_threshold, inventory_status, last_updated) VALUES
(1, 200, 0, 20, 'IN_STOCK', NOW()),
(2, 150, 0, 15, 'IN_STOCK', NOW()),
(3, 180, 0, 20, 'IN_STOCK', NOW()),
(4, 120, 0, 15, 'IN_STOCK', NOW()),
(5, 90, 0, 10, 'IN_STOCK', NOW()),
(6, 60, 0, 10, 'IN_STOCK', NOW()),
(7, 100, 0, 10, 'IN_STOCK', NOW()),
(8, 70, 0, 10, 'IN_STOCK', NOW()),
(9, 8, 0, 10, 'LOW_STOCK', NOW()),
(10, 40, 0, 5, 'IN_STOCK', NOW()),
(11, 75, 0, 10, 'IN_STOCK', NOW()),
(12, 35, 0, 5, 'IN_STOCK', NOW()),
(13, 0, 0, 10, 'OUT_OF_STOCK', NOW()),
(14, 65, 0, 10, 'IN_STOCK', NOW());

-- ===== Product Images (front image per variant, product-level thumbnail) =====
INSERT INTO product_images (product_id, variant_id, image_url, view_type, description, is_thumbnail, sort_order, uploaded_at) VALUES
(1, 1, 'https://picsum.photos/seed/turmeric-100/500/500', 'front', 'Turmeric Powder 100g', b'1', 1, NOW()),
(1, 2, 'https://picsum.photos/seed/turmeric-500/500/500', 'front', 'Turmeric Powder 500g', b'1', 1, NOW()),
(2, 3, 'https://picsum.photos/seed/kashmiri-chilli-100/500/500', 'front', 'Kashmiri Red Chilli 100g', b'1', 1, NOW()),
(2, 4, 'https://picsum.photos/seed/kashmiri-chilli-500/500/500', 'front', 'Kashmiri Red Chilli 500g', b'1', 1, NOW()),
(3, 5, 'https://picsum.photos/seed/assam-tea-250/500/500', 'front', 'Assam Black Tea 250g', b'1', 1, NOW()),
(3, 6, 'https://picsum.photos/seed/assam-tea-500/500/500', 'front', 'Assam Black Tea 500g', b'1', 1, NOW()),
(4, 7, 'https://picsum.photos/seed/filter-coffee-200/500/500', 'front', 'Filter Coffee Powder 200g', b'1', 1, NOW()),
(4, 8, 'https://picsum.photos/seed/filter-coffee-500/500/500', 'front', 'Filter Coffee Powder 500g', b'1', 1, NOW()),
(5, 9, 'https://picsum.photos/seed/cashews-250/500/500', 'front', 'Premium Cashews 250g', b'1', 1, NOW()),
(5, 10, 'https://picsum.photos/seed/cashews-1000/500/500', 'front', 'Premium Cashews 1kg', b'1', 1, NOW()),
(6, 11, 'https://picsum.photos/seed/almonds-250/500/500', 'front', 'California Almonds 250g', b'1', 1, NOW()),
(6, 12, 'https://picsum.photos/seed/almonds-1000/500/500', 'front', 'California Almonds 1kg', b'1', 1, NOW()),
(7, 13, 'https://picsum.photos/seed/basil-50/500/500', 'front', 'Dried Basil Leaves 50g', b'1', 1, NOW()),
(8, 14, 'https://picsum.photos/seed/curryleaves-100/500/500', 'front', 'Curry Leaves Powder 100g', b'1', 1, NOW());

-- ===== Banners =====
INSERT INTO banners (title, image_url, redirect_url, banner_type, uploaded_by, uploaded_at) VALUES
('Fresh Spices, Delivered', 'https://picsum.photos/seed/banner-spices/1200/400', '/search?category=1', 'Homepage', NULL, NOW()),
('Wholesale Pricing for Bulk Buyers', 'https://picsum.photos/seed/banner-wholesale/1200/400', '/home', 'Homepage', NULL, NOW());
