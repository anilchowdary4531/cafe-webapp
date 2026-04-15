-- Seed dedicated super admin test account.
-- Default password hash corresponds to Password@123 (bcrypt).
INSERT INTO app_user (email, password_hash, role)
VALUES (
  'superadmin.test@sunsetcafe.local',
  '$2a$10$TWh4UXM5fA8ixNaw9f6W7uovqRs36A2v9QY9/VYRWxqsD0f5NytA6',
  'SUPER_ADMIN'
)
ON CONFLICT (email) DO UPDATE
SET role = EXCLUDED.role,
    password_hash = EXCLUDED.password_hash;

