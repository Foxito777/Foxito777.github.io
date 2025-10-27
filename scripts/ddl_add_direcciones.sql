-- ddl_add_direcciones.sql
-- Script para crear la tabla `direcciones` y añadir la columna `direccion_id` en `clientes`
-- Ajusta los tipos y tamaños según tu motor (ejemplo pensado para MySQL/MariaDB).

-- 1) Crear tabla direcciones
-- 1) Crear tabla direcciones (con cliente_id FK)
CREATE TABLE IF NOT EXISTS direcciones (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  cliente_id BIGINT,
  departamento VARCHAR(100),
  provincia VARCHAR(100),
  distrito VARCHAR(100),
  detalle VARCHAR(300),
  numero VARCHAR(50),
  telefono VARCHAR(30),
  direccion_completa VARCHAR(500),
  INDEX idx_direcciones_cliente_id (cliente_id)
);

-- 2) Crear la FK que referencia a clientes.id. Si ya existe, la instrucción fallará; revísalo antes de ejecutar en producción.
ALTER TABLE direcciones
  ADD CONSTRAINT fk_direcciones_cliente
  FOREIGN KEY (cliente_id) REFERENCES clientes(id)
  ON DELETE CASCADE;

-- Fin del script DDL
