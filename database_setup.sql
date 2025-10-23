-- Configuración de base de datos MySQL para Juledtoys
-- Ejecuta estos comandos en tu cliente MySQL (MySQL Workbench, phpMyAdmin, etc.)

-- 1. Crear la base de datos
CREATE DATABASE IF NOT EXISTS juledtoys_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. Usar la base de datos
USE juledtoys_db;

-- 3. Crear usuario (opcional, si quieres un usuario específico)
-- CREATE USER 'juledtoys_user'@'localhost' IDENTIFIED BY 'password123';
-- GRANT ALL PRIVILEGES ON juledtoys_db.* TO 'juledtoys_user'@'localhost';
-- FLUSH PRIVILEGES;

-- Nota: Las tablas se crearán automáticamente por Hibernate al ejecutar la aplicación
-- debido a la configuración spring.jpa.hibernate.ddl-auto=update

-- Verificar que la base de datos fue creada
SHOW DATABASES LIKE 'juledtoys_db';