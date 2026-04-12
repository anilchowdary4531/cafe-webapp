-- Improve key menu images and add Chicken Nuggets with a dedicated image.

UPDATE menu_item
SET image_url = 'https://source.unsplash.com/640x480/?french-fries'
WHERE name = 'French Fries';

UPDATE menu_item
SET image_url = 'https://source.unsplash.com/640x480/?chicken-burger'
WHERE name = 'Chicken Burger';

INSERT INTO menu_item (name, category, price, description, image_url, restricted, available)
VALUES (
  'Chicken Nuggets',
  'Snacks',
  5.90,
  'Crispy chicken nuggets with dip',
  'https://source.unsplash.com/640x480/?chicken-nuggets',
  FALSE,
  TRUE
)
ON CONFLICT DO NOTHING;

