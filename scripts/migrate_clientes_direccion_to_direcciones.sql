-- migrate_clientes_direccion_to_direcciones.sql
-- Script específico para MySQL para migrar datos desde la columna vieja
-- `clientes.direccion` hacia la nueva tabla `direcciones` sin depender de coincidencias por texto.
-- Recomendación: hacer backup antes de ejecutar en producción.

-- 1) Añadir columna temporal cliente_id a direcciones para uso en la migración
ALTER TABLE direcciones
	ADD COLUMN IF NOT EXISTS cliente_id BIGINT;

-- 2) Insertar una fila en 'direcciones' por cada cliente que tenga texto en la columna antigua 'direccion'
INSERT INTO direcciones (cliente_id, direccion_completa, telefono)
SELECT id AS cliente_id, direccion AS direccion_completa, telefono
FROM clientes
WHERE direccion IS NOT NULL AND TRIM(direccion) <> '';
-- 3) Verificación rápida: listar las direcciones recién creadas y su cliente asociado
SELECT id, cliente_id, direccion_completa FROM direcciones WHERE cliente_id IS NOT NULL LIMIT 50;

-- 4) (Opcional) Si todo está correcto, eliminar la columna antigua en clientes y la columna temporal cliente_id
-- (si no quieres mantener cliente_id en direcciones como referencia explícita)
-- ALTER TABLE clientes DROP COLUMN direccion;
-- ALTER TABLE direcciones DROP COLUMN cliente_id;

-- NOTA: Si prefieres mantener una copia del enlace cliente->dirección, omite el DROP de cliente_id.

-- Fin del script de migración de datos (MySQL)
