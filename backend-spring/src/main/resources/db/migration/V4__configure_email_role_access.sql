-- V4__configure_email_role_access.sql
-- Enforce role access by email: one admin, one staff, all others customer.

-- Default password hash corresponds to Password@123 (bcrypt)
INSERT INTO app_user (email, password_hash, role)
VALUES
  ('anilchowdarya8@gmail.com', '$2a$10$TWh4UXM5fA8ixNaw9f6W7uovqRs36A2v9QY9/VYRWxqsD0f5NytA6', 'ADMIN')
ON CONFLICT (email) DO UPDATE
SET role = EXCLUDED.role;

INSERT INTO app_user (email, password_hash, role)
VALUES
  ('staff@sunsetcafe.local', '$2a$10$TWh4UXM5fA8ixNaw9f6W7uovqRs36A2v9QY9/VYRWxqsD0f5NytA6', 'STAFF')
ON CONFLICT (email) DO UPDATE
SET role = EXCLUDED.role;

UPDATE app_user
SET role = 'CUSTOMER'
WHERE LOWER(email) NOT IN ('anilchowdarya8@gmail.com', 'staff@sunsetcafe.local');

