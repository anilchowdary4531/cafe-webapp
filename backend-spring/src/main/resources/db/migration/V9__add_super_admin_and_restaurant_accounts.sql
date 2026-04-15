CREATE TABLE IF NOT EXISTS restaurant_account (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  slug VARCHAR(255) NOT NULL UNIQUE,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE app_user
  ADD COLUMN IF NOT EXISTS restaurant_id BIGINT,
  ADD COLUMN IF NOT EXISTS is_primary_admin BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS delegated_by_user_id BIGINT,
  ADD COLUMN IF NOT EXISTS delegated_at TIMESTAMP;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'fk_app_user_restaurant'
  ) THEN
    ALTER TABLE app_user
      ADD CONSTRAINT fk_app_user_restaurant
      FOREIGN KEY (restaurant_id)
      REFERENCES restaurant_account(id)
      ON DELETE SET NULL;
  END IF;
END $$;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'fk_app_user_delegated_by'
  ) THEN
    ALTER TABLE app_user
      ADD CONSTRAINT fk_app_user_delegated_by
      FOREIGN KEY (delegated_by_user_id)
      REFERENCES app_user(id)
      ON DELETE SET NULL;
  END IF;
END $$;

INSERT INTO restaurant_account (name, slug)
VALUES ('Sunset Cafe - Legacy', 'sunset-cafe-legacy')
ON CONFLICT (slug) DO NOTHING;

UPDATE app_user
SET restaurant_id = (
  SELECT id FROM restaurant_account WHERE slug = 'sunset-cafe-legacy'
)
WHERE restaurant_id IS NULL
  AND role <> 'SUPER_ADMIN';

UPDATE app_user
SET is_primary_admin = TRUE
WHERE role = 'ADMIN'
  AND id IN (
    SELECT DISTINCT ON (restaurant_id) id
    FROM app_user
    WHERE role = 'ADMIN' AND restaurant_id IS NOT NULL
    ORDER BY restaurant_id, created_at ASC
  );

CREATE UNIQUE INDEX IF NOT EXISTS ux_app_user_one_primary_admin_per_restaurant
  ON app_user(restaurant_id)
  WHERE role = 'ADMIN' AND is_primary_admin = TRUE;

INSERT INTO app_user (email, password_hash, role)
VALUES (
  'superadmin@sunsetcafe.local',
  '$2a$10$TWh4UXM5fA8ixNaw9f6W7uovqRs36A2v9QY9/VYRWxqsD0f5NytA6',
  'SUPER_ADMIN'
)
ON CONFLICT (email) DO UPDATE
SET role = EXCLUDED.role;

