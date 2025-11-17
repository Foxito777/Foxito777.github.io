-- Script para corregir estados de pedidos que no coinciden con el nuevo enum
-- Agrega el estado PENDIENTE_PAGO y actualiza estados inválidos

-- Ver qué estados existen actualmente
SELECT DISTINCT estado, COUNT(*) as cantidad FROM pedidos GROUP BY estado;

-- Si hay estados que no están en el enum nuevo, actualizarlos temporalmente a PENDIENTE
-- Esto evita el error "Data truncated for column 'estado'"
UPDATE pedidos 
SET estado = 'PENDIENTE' 
WHERE estado NOT IN ('PENDIENTE', 'CONFIRMADO', 'EN_PREPARACION', 'EN_CAMINO', 'ENTREGADO', 'CANCELADO', 'PENDIENTE_PAGO');

-- Verificar que todos los estados sean válidos
SELECT DISTINCT estado, COUNT(*) as cantidad FROM pedidos GROUP BY estado;
