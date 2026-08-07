USE spice_store;

-- Categories
UPDATE categories SET image = 'https://placehold.co/600x400/c2410c/ffffff?font=roboto&text=Spices+%26+Masalas' WHERE id = 1;
UPDATE categories SET image = 'https://placehold.co/600x400/5c4033/ffffff?font=roboto&text=Tea+%26+Coffee' WHERE id = 2;
UPDATE categories SET image = 'https://placehold.co/600x400/b45309/ffffff?font=roboto&text=Dry+Fruits+%26+Nuts' WHERE id = 3;
UPDATE categories SET image = 'https://placehold.co/600x400/166534/ffffff?font=roboto&text=Herbs+%26+Seasonings' WHERE id = 4;

-- Product thumbnails
UPDATE products SET thumbnail = 'https://placehold.co/500x500/d97706/ffffff?font=roboto&text=Turmeric+Powder' WHERE id = 1;
UPDATE products SET thumbnail = 'https://placehold.co/500x500/b91c1c/ffffff?font=roboto&text=Kashmiri+Red+Chilli' WHERE id = 2;
UPDATE products SET thumbnail = 'https://placehold.co/500x500/5c4033/ffffff?font=roboto&text=Assam+Black+Tea' WHERE id = 3;
UPDATE products SET thumbnail = 'https://placehold.co/500x500/6f4518/ffffff?font=roboto&text=Filter+Coffee' WHERE id = 4;
UPDATE products SET thumbnail = 'https://placehold.co/500x500/b45309/ffffff?font=roboto&text=Premium+Cashews' WHERE id = 5;
UPDATE products SET thumbnail = 'https://placehold.co/500x500/a67c52/ffffff?font=roboto&text=California+Almonds' WHERE id = 6;
UPDATE products SET thumbnail = 'https://placehold.co/500x500/166534/ffffff?font=roboto&text=Dried+Basil' WHERE id = 7;
UPDATE products SET thumbnail = 'https://placehold.co/500x500/14532d/ffffff?font=roboto&text=Curry+Leaves' WHERE id = 8;

-- Variant images
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/d97706/ffffff?font=roboto&text=Turmeric+100g' WHERE id = 1;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/d97706/ffffff?font=roboto&text=Turmeric+500g' WHERE id = 2;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/b91c1c/ffffff?font=roboto&text=Chilli+100g' WHERE id = 3;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/b91c1c/ffffff?font=roboto&text=Chilli+500g' WHERE id = 4;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/5c4033/ffffff?font=roboto&text=Black+Tea+250g' WHERE id = 5;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/5c4033/ffffff?font=roboto&text=Black+Tea+500g' WHERE id = 6;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/6f4518/ffffff?font=roboto&text=Coffee+200g' WHERE id = 7;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/6f4518/ffffff?font=roboto&text=Coffee+500g' WHERE id = 8;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/b45309/ffffff?font=roboto&text=Cashews+250g' WHERE id = 9;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/b45309/ffffff?font=roboto&text=Cashews+1kg' WHERE id = 10;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/a67c52/ffffff?font=roboto&text=Almonds+250g' WHERE id = 11;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/a67c52/ffffff?font=roboto&text=Almonds+1kg' WHERE id = 12;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/166534/ffffff?font=roboto&text=Basil+50g' WHERE id = 13;
UPDATE product_variants SET image_url = 'https://placehold.co/500x500/14532d/ffffff?font=roboto&text=Curry+Leaves+100g' WHERE id = 14;

-- Product images (front images, keyed by variant_id to match)
UPDATE product_images SET image_url = 'https://placehold.co/500x500/d97706/ffffff?font=roboto&text=Turmeric+100g' WHERE variant_id = 1;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/d97706/ffffff?font=roboto&text=Turmeric+500g' WHERE variant_id = 2;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/b91c1c/ffffff?font=roboto&text=Chilli+100g' WHERE variant_id = 3;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/b91c1c/ffffff?font=roboto&text=Chilli+500g' WHERE variant_id = 4;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/5c4033/ffffff?font=roboto&text=Black+Tea+250g' WHERE variant_id = 5;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/5c4033/ffffff?font=roboto&text=Black+Tea+500g' WHERE variant_id = 6;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/6f4518/ffffff?font=roboto&text=Coffee+200g' WHERE variant_id = 7;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/6f4518/ffffff?font=roboto&text=Coffee+500g' WHERE variant_id = 8;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/b45309/ffffff?font=roboto&text=Cashews+250g' WHERE variant_id = 9;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/b45309/ffffff?font=roboto&text=Cashews+1kg' WHERE variant_id = 10;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/a67c52/ffffff?font=roboto&text=Almonds+250g' WHERE variant_id = 11;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/a67c52/ffffff?font=roboto&text=Almonds+1kg' WHERE variant_id = 12;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/166534/ffffff?font=roboto&text=Basil+50g' WHERE variant_id = 13;
UPDATE product_images SET image_url = 'https://placehold.co/500x500/14532d/ffffff?font=roboto&text=Curry+Leaves+100g' WHERE variant_id = 14;

-- Banners
UPDATE banners SET image_url = 'https://placehold.co/1200x400/c2410c/ffffff?font=roboto&text=Fresh+Spices%2C+Delivered' WHERE id = 1;
UPDATE banners SET image_url = 'https://placehold.co/1200x400/78350f/ffffff?font=roboto&text=Wholesale+Pricing+for+Bulk+Buyers' WHERE id = 2;
