-- MySQL remote access setup for JuledToys
-- Run these commands on the MySQL SERVER (as root or a privileged user)

CREATE DATABASE IF NOT EXISTS juledtoy_bds
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create or update app user for remote access
CREATE USER IF NOT EXISTS 'juledtoy_adminbd'@'%' IDENTIFIED BY 'Juledbase2025';

-- Ensure mysql_native_password for broader client compatibility
ALTER USER 'juledtoy_adminbd'@'%'
  IDENTIFIED WITH mysql_native_password BY 'Juledbase2025';

-- Grant privileges to the database
GRANT ALL PRIVILEGES ON juledtoy_bds.* TO 'juledtoy_adminbd'@'%';
FLUSH PRIVILEGES;

-- Optional: verify
-- SELECT user, host, plugin FROM mysql.user WHERE user = 'juledtoy_adminbd';
-- SHOW GRANTS FOR 'juledtoy_adminbd'@'%';
