-- Normalize and harden super admin test seed.
-- This migration is safe even if V10 already ran in another environment.

-- Ensure any case-variant of the same email is promoted to SUPER_ADMIN and gets test password.
UPDATE app_user
SET role = 'SUPER_ADMIN',
    password_hash = '$2a$10$TWh4UXM5fA8ixNaw9f6W7uovqRs36A2v9QY9/VYRWxqsD0f5NytA6'
WHERE LOWER(email) = 'superadmin.test@sunsetcafe.local'
  AND (
    role IS DISTINCT FROM 'SUPER_ADMIN'
    OR password_hash IS DISTINCT FROM '$2a$10$TWh4UXM5fA8ixNaw9f6W7uovqRs36A2v9QY9/VYRWxqsD0f5NytA6'
  );

-- Insert the test account only when no normalized match exists.
INSERT INTO app_user (email, password_hash, role)
SELECT
  'superadmin.test@sunsetcafe.local',
  '$2a$10$TWh4UXM5fA8ixNaw9f6W7uovqRs36A2v9QY9/VYRWxqsD0f5NytA6',
  'SUPER_ADMIN'
WHERE NOT EXISTS (
  SELECT 1
  FROM app_user
  WHERE LOWER(email) = 'superadmin.test@sunsetcafe.local'
);

