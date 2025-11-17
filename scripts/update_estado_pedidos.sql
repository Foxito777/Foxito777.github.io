-- Script para actualizar el sistema de estados de pedidos
-- Agrega el nuevo estado RECIBIDO y actualiza pedidos existentes

-- 1. Los pedidos en estado PENDIENTE se mantienen como están
-- 2. Este script debe ejecutarse antes de iniciar la aplicación

-- Actualizar pedidos PENDIENTES a RECIBIDO (opcional, solo si quieres migrar los existentes)
-- UPDATE pedidos SET estado = 'RECIBIDO' WHERE estado = 'PENDIENTE';

-- Nota: Los nuevos pedidos automáticamente se crearán con estado RECIBIDO
-- No es necesario hacer nada más si prefieres que los pedidos antiguos mantengan su estado actual
