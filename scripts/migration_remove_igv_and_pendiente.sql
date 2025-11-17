-- ============================================================================
-- SCRIPT DE MIGRACIÓN: Eliminar IGV y Estado PENDIENTE
-- Fecha: 30 de octubre de 2025
-- Descripción: 
--   1. Actualizar pedidos para que IGV = 0 y Total = Subtotal
--   2. Cambiar todos los estados PENDIENTE a RECIBIDO
-- ============================================================================

-- Mostrar información antes de la migración
SELECT 
    'ANTES DE LA MIGRACIÓN' as Info,
    COUNT(*) as 'Total Pedidos',
    SUM(CASE WHEN estado = 'PENDIENTE' THEN 1 ELSE 0 END) as 'Pedidos PENDIENTE',
    SUM(CASE WHEN igv > 0 THEN 1 ELSE 0 END) as 'Pedidos con IGV'
FROM pedidos;

-- ============================================================================
-- PASO 1: Actualizar IGV a 0 y ajustar Total = Subtotal
-- ============================================================================

-- Respaldar datos actuales (opcional, comentar si no se necesita)
-- CREATE TABLE IF NOT EXISTS pedidos_backup_20251030 AS SELECT * FROM pedidos;

-- Actualizar todos los pedidos
UPDATE pedidos
SET 
    igv = 0.00,
    total = subtotal
WHERE igv != 0.00;

SELECT 
    'Paso 1 completado' as Status,
    COUNT(*) as 'Pedidos actualizados (IGV)'
FROM pedidos 
WHERE igv = 0.00;

-- ============================================================================
-- PASO 2: Cambiar estado PENDIENTE a RECIBIDO
-- ============================================================================

UPDATE pedidos
SET estado = 'RECIBIDO'
WHERE estado = 'PENDIENTE';

SELECT 
    'Paso 2 completado' as Status,
    COUNT(*) as 'Pedidos cambiados (PENDIENTE → RECIBIDO)'
FROM pedidos 
WHERE estado = 'RECIBIDO';

-- ============================================================================
-- VERIFICACIÓN FINAL
-- ============================================================================

-- Verificar que no queden pedidos con IGV > 0
SELECT 
    'VERIFICACIÓN IGV' as Tipo,
    COUNT(*) as Cantidad
FROM pedidos 
WHERE igv > 0;

-- Verificar que no queden pedidos con estado PENDIENTE
SELECT 
    'VERIFICACIÓN PENDIENTE' as Tipo,
    COUNT(*) as Cantidad
FROM pedidos 
WHERE estado = 'PENDIENTE';

-- Mostrar resumen de estados actuales
SELECT 
    estado,
    COUNT(*) as cantidad
FROM pedidos
GROUP BY estado
ORDER BY 
    CASE estado
        WHEN 'RECIBIDO' THEN 1
        WHEN 'CONFIRMADO' THEN 2
        WHEN 'EN_PREPARACION' THEN 3
        WHEN 'EN_CAMINO' THEN 4
        WHEN 'ENTREGADO' THEN 5
        WHEN 'CANCELADO' THEN 6
        ELSE 7
    END;

-- Mostrar información después de la migración
SELECT 
    'DESPUÉS DE LA MIGRACIÓN' as Info,
    COUNT(*) as 'Total Pedidos',
    SUM(CASE WHEN igv = 0 THEN 1 ELSE 0 END) as 'Pedidos sin IGV',
    SUM(CASE WHEN total = subtotal THEN 1 ELSE 0 END) as 'Pedidos Total=Subtotal'
FROM pedidos;

-- ============================================================================
-- OPCIONAL: Modificar la estructura de la tabla (si se desea)
-- ============================================================================

-- Si se desea eliminar completamente la columna IGV (CUIDADO: irreversible)
-- ALTER TABLE pedidos DROP COLUMN igv;

-- Si se desea cambiar el constraint del enum de estado (MySQL 8.0+)
-- ALTER TABLE pedidos MODIFY COLUMN estado ENUM(
--     'RECIBIDO', 
--     'CONFIRMADO', 
--     'EN_PREPARACION', 
--     'EN_CAMINO', 
--     'ENTREGADO', 
--     'CANCELADO'
-- ) NOT NULL DEFAULT 'RECIBIDO';

-- ============================================================================
-- FIN DEL SCRIPT
-- ============================================================================

SELECT '✅ MIGRACIÓN COMPLETADA EXITOSAMENTE' as Resultado;
