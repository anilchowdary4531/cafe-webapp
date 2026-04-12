-- ── Menu Items ───────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS menu_item (
  id          BIGSERIAL    PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  category    VARCHAR(100) NOT NULL,
  price       NUMERIC(10,2) NOT NULL DEFAULT 0,
  description VARCHAR(500),
  restricted  BOOLEAN      NOT NULL DEFAULT FALSE,
  available   BOOLEAN      NOT NULL DEFAULT TRUE,
  created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── Orders ────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cafe_order (
  id                    BIGSERIAL     PRIMARY KEY,
  table_label           VARCHAR(64)   NOT NULL,
  note                  TEXT,
  status                VARCHAR(32)   NOT NULL DEFAULT 'received',
  subtotal              NUMERIC(10,2) NOT NULL DEFAULT 0,
  tax                   NUMERIC(10,2) NOT NULL DEFAULT 0,
  total                 NUMERIC(10,2) NOT NULL DEFAULT 0,
  customer_phone        VARCHAR(30),
  customer_phone_masked VARCHAR(30),
  customer_email        VARCHAR(255),
  payment_status        VARCHAR(32)   NOT NULL DEFAULT 'unpaid',
  paid_at               TIMESTAMP,
  phone_verified_at     TIMESTAMP,
  created_at            TIMESTAMP     NOT NULL DEFAULT NOW(),
  updated_at            TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_cafe_order_table_label ON cafe_order (table_label, created_at DESC);

-- ── Order Items ───────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS order_item (
  id           BIGSERIAL     PRIMARY KEY,
  order_id     BIGINT        NOT NULL REFERENCES cafe_order(id) ON DELETE CASCADE,
  menu_item_id BIGINT,
  name         VARCHAR(255)  NOT NULL,
  qty          INT           NOT NULL DEFAULT 1,
  price        NUMERIC(10,2) NOT NULL DEFAULT 0,
  restricted   BOOLEAN       NOT NULL DEFAULT FALSE
);

-- ── Service Requests ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS service_request (
  id                    BIGSERIAL    PRIMARY KEY,
  table_label           VARCHAR(64)  NOT NULL,
  type                  VARCHAR(100) NOT NULL,
  note                  TEXT,
  status                VARCHAR(32)  NOT NULL DEFAULT 'pending',
  items                 JSONB,
  customer_phone        VARCHAR(30),
  customer_phone_masked VARCHAR(30),
  phone_verified_at     TIMESTAMP,
  created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
  updated_at            TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_service_request_table_label ON service_request (table_label, created_at DESC);

-- ── Seed default menu ─────────────────────────────────────────────────────────
INSERT INTO menu_item (name, category, price, description, restricted) VALUES
  ('Veg Sandwich',      'Food',       5.50, 'Fresh grilled sandwich',       FALSE),
  ('Chicken Burger',    'Food',       7.90, 'Served with sauce',            FALSE),
  ('French Fries',      'Snacks',     3.80, 'Crispy salted fries',          FALSE),
  ('Samosa Plate',      'Snacks',     4.20, '2 pieces with chutney',        FALSE),
  ('Cappuccino',        'Coffee',     4.00, 'Classic foam coffee',          FALSE),
  ('Cold Coffee',       'Coffee',     4.80, 'Chilled with ice',             FALSE),
  ('Masala Tea',        'Tea',        2.50, 'Indian spiced tea',            FALSE),
  ('Green Tea',         'Tea',        2.30, 'Light and refreshing',         FALSE),
  ('Orange Juice',      'Juices',     3.90, 'Fresh juice',                  FALSE),
  ('Mango Shake',       'Juices',     4.50, 'Thick and sweet',              FALSE),
  ('Vanilla Scoop',     'Ice Cream',  2.90, 'Single scoop',                 FALSE),
  ('Chocolate Sundae',  'Ice Cream',  4.90, 'With chocolate syrup',         FALSE),
  ('Cigarette Request', 'Cigarettes', 0.00, 'Staff approval required',      TRUE)
ON CONFLICT DO NOTHING;

