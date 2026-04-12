ALTER TABLE menu_item
ADD COLUMN IF NOT EXISTS image_url VARCHAR(1000);

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/f6f3ee/8b5e3c?text=Veg+Sandwich'
WHERE name = 'Veg Sandwich' AND (image_url IS NULL OR image_url = '');

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/f6f3ee/8b5e3c?text=Chicken+Burger'
WHERE name = 'Chicken Burger' AND (image_url IS NULL OR image_url = '');

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/f6f3ee/8b5e3c?text=French+Fries'
WHERE name = 'French Fries' AND (image_url IS NULL OR image_url = '');

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/f6f3ee/8b5e3c?text=Samosa+Plate'
WHERE name = 'Samosa Plate' AND (image_url IS NULL OR image_url = '');

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/e6f7f5/0f766e?text=Cappuccino'
WHERE name = 'Cappuccino' AND (image_url IS NULL OR image_url = '');

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/e6f7f5/0f766e?text=Cold+Coffee'
WHERE name = 'Cold Coffee' AND (image_url IS NULL OR image_url = '');

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/f5efe6/8b5e3c?text=Masala+Tea'
WHERE name = 'Masala Tea' AND (image_url IS NULL OR image_url = '');

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/f5efe6/8b5e3c?text=Green+Tea'
WHERE name = 'Green Tea' AND (image_url IS NULL OR image_url = '');

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/fff7ed/f97316?text=Orange+Juice'
WHERE name = 'Orange Juice' AND (image_url IS NULL OR image_url = '');

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/fff7ed/f97316?text=Mango+Shake'
WHERE name = 'Mango Shake' AND (image_url IS NULL OR image_url = '');

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/fdf2f8/db2777?text=Vanilla+Scoop'
WHERE name = 'Vanilla Scoop' AND (image_url IS NULL OR image_url = '');

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/fdf2f8/db2777?text=Chocolate+Sundae'
WHERE name = 'Chocolate Sundae' AND (image_url IS NULL OR image_url = '');

UPDATE menu_item SET image_url = 'https://placehold.co/600x400/fee2e2/b91c1c?text=Staff+Approval'
WHERE name = 'Cigarette Request' AND (image_url IS NULL OR image_url = '');

