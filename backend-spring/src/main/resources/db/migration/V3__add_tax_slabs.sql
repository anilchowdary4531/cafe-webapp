-- V3__add_tax_slabs.sql
-- Tax Slab Management Table

CREATE TABLE IF NOT EXISTS tax_slab (
  id          BIGSERIAL    PRIMARY KEY,
  name        VARCHAR(255) NOT NULL UNIQUE,
  rate        NUMERIC(10,4) NOT NULL,
  description TEXT,
  is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
  created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Create index for faster queries
CREATE INDEX IF NOT EXISTS idx_tax_slab_is_active ON tax_slab(is_active);

-- Seed default tax slabs
INSERT INTO tax_slab (name, rate, description, is_active)
VALUES
  ('Standard Tax (5%)', 0.05, 'Default tax rate for regular items', true),
  ('Reduced Tax (2%)', 0.02, 'Reduced tax rate for essential items', true),
  ('No Tax (0%)', 0.00, 'Tax-free items (e.g., basic food)', true),
  ('Premium Tax (18%)', 0.18, 'Higher tax for premium items', true)
ON CONFLICT DO NOTHING;

