-- =====================================================
-- SCRIPT SQL PARA MÓDULOS DE PEDIDOS Y REPORTES
-- JuledToys - Sistema de Gestión - MySQL
-- Base de datos: juledtoy_bds
-- Servidor: 45.79.40.132:3306
-- =====================================================

USE juledtoy_bds;

-- =====================================================
-- TABLAS PRINCIPALES
-- =====================================================

-- Tabla: pedidos
CREATE TABLE IF NOT EXISTS pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_pedido VARCHAR(20) UNIQUE NOT NULL,
    cliente_id BIGINT NOT NULL,
    fecha_pedido DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    subtotal DECIMAL(10,2) NOT NULL,
    igv DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    direccion_envio VARCHAR(500) NOT NULL,
    telefono_contacto VARCHAR(20),
    metodo_pago VARCHAR(50),
    observaciones TEXT,
    fecha_entrega_estimada DATETIME,
    fecha_entrega_real DATETIME,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE RESTRICT,
    INDEX idx_numero_pedido (numero_pedido),
    INDEX idx_cliente (cliente_id),
    INDEX idx_estado (estado),
    INDEX idx_fecha_pedido (fecha_pedido),
    INDEX idx_estado_fecha (estado, fecha_pedido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: items_pedido
CREATE TABLE IF NOT EXISTS items_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    descuento DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT,
    INDEX idx_pedido (pedido_id),
    INDEX idx_producto (producto_id),
    INDEX idx_producto_cantidad (producto_id, cantidad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- DATOS DE EJEMPLO (Opcional - comentar si no se desea)
-- =====================================================

-- IMPORTANTE: Verifica que existan clientes con IDs 1-5 y productos con IDs 1-10
-- Si no existen, ajusta los IDs o comenta esta sección

-- Insertar pedidos de ejemplo
INSERT INTO pedidos (numero_pedido, cliente_id, fecha_pedido, estado, subtotal, igv, total, direccion_envio, telefono_contacto, metodo_pago, observaciones, fecha_entrega_estimada)
VALUES
('PED-20251026-0001', 1, '2025-10-20 10:30:00', 'ENTREGADO', 250.00, 45.00, 295.00, 'Av. Principal 123, Lima', '987654321', 'Tarjeta', 'Entrega en horario de oficina', '2025-10-22 14:00:00'),
('PED-20251026-0002', 1, '2025-10-21 15:45:00', 'ENVIADO', 180.00, 32.40, 212.40, 'Jr. Los Rosales 456, San Isidro', '965432178', 'Efectivo', NULL, '2025-10-26 18:00:00'),
('PED-20251026-0003', 1, '2025-10-23 09:15:00', 'PREPARANDO', 420.00, 75.60, 495.60, 'Calle Las Flores 789, Miraflores', '912345678', 'Yape/Plin', 'Llamar antes de entregar', '2025-10-27 16:00:00'),
('PED-20251026-0004', 1, '2025-10-24 11:20:00', 'CONFIRMADO', 150.00, 27.00, 177.00, 'Av. Arequipa 321, Lince', '998765432', 'Transferencia', NULL, '2025-10-28 12:00:00'),
('PED-20251026-0005', 1, '2025-10-25 16:30:00', 'PENDIENTE', 320.00, 57.60, 377.60, 'Calle Lima 654, Surco', '987123456', 'Tarjeta', 'Entregar en la tarde', '2025-10-29 15:00:00'),
('PED-20251026-0006', 1, '2025-10-26 08:00:00', 'ENTREGADO', 890.00, 160.20, 1050.20, 'Av. Javier Prado 1234, San Borja', '943218765', 'Efectivo', 'Pedido urgente', '2025-10-26 20:00:00'),
('PED-20251026-0007', 1, '2025-10-19 13:45:00', 'ENTREGADO', 560.00, 100.80, 660.80, 'Av. Principal 123, Lima', '987654321', 'Yape/Plin', NULL, '2025-10-21 10:00:00'),
('PED-20251026-0008', 1, '2025-10-18 10:00:00', 'CANCELADO', 200.00, 36.00, 236.00, 'Jr. Comercio 987, Pueblo Libre', '921345678', 'Tarjeta', 'Cliente canceló', NULL)
ON DUPLICATE KEY UPDATE numero_pedido=numero_pedido;

-- Insertar items de pedido de ejemplo
INSERT INTO items_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal, descuento)
SELECT 1, 1, 2, 75.00, 150.00, 0.00 WHERE EXISTS (SELECT 1 FROM pedidos WHERE id = 1) AND EXISTS (SELECT 1 FROM productos WHERE id = 1)
UNION ALL
SELECT 1, 2, 1, 100.00, 100.00, 0.00 WHERE EXISTS (SELECT 1 FROM pedidos WHERE id = 1) AND EXISTS (SELECT 1 FROM productos WHERE id = 2)
UNION ALL
SELECT 2, 1, 3, 60.00, 180.00, 0.00 WHERE EXISTS (SELECT 1 FROM pedidos WHERE id = 2) AND EXISTS (SELECT 1 FROM productos WHERE id = 1)
UNION ALL
SELECT 3, 2, 1, 200.00, 200.00, 0.00 WHERE EXISTS (SELECT 1 FROM pedidos WHERE id = 3) AND EXISTS (SELECT 1 FROM productos WHERE id = 2)
UNION ALL
SELECT 3, 1, 2, 110.00, 220.00, 0.00 WHERE EXISTS (SELECT 1 FROM pedidos WHERE id = 3) AND EXISTS (SELECT 1 FROM productos WHERE id = 1)
UNION ALL
SELECT 4, 1, 2, 75.00, 150.00, 0.00 WHERE EXISTS (SELECT 1 FROM pedidos WHERE id = 4) AND EXISTS (SELECT 1 FROM productos WHERE id = 1)
UNION ALL
SELECT 5, 2, 4, 80.00, 320.00, 0.00 WHERE EXISTS (SELECT 1 FROM pedidos WHERE id = 5) AND EXISTS (SELECT 1 FROM productos WHERE id = 2)
UNION ALL
SELECT 6, 1, 1, 450.00, 450.00, 0.00 WHERE EXISTS (SELECT 1 FROM pedidos WHERE id = 6) AND EXISTS (SELECT 1 FROM productos WHERE id = 1)
UNION ALL
SELECT 6, 2, 2, 220.00, 440.00, 0.00 WHERE EXISTS (SELECT 1 FROM pedidos WHERE id = 6) AND EXISTS (SELECT 1 FROM productos WHERE id = 2)
UNION ALL
SELECT 7, 1, 4, 75.00, 300.00, 0.00 WHERE EXISTS (SELECT 1 FROM pedidos WHERE id = 7) AND EXISTS (SELECT 1 FROM productos WHERE id = 1)
UNION ALL
SELECT 7, 2, 1, 260.00, 260.00, 0.00 WHERE EXISTS (SELECT 1 FROM pedidos WHERE id = 7) AND EXISTS (SELECT 1 FROM productos WHERE id = 2)
UNION ALL
SELECT 8, 1, 2, 100.00, 200.00, 0.00 WHERE EXISTS (SELECT 1 FROM pedidos WHERE id = 8) AND EXISTS (SELECT 1 FROM productos WHERE id = 1);

-- =====================================================
-- VISTAS ÚTILES PARA REPORTES
-- =====================================================

-- Vista: Resumen de ventas por producto
DROP VIEW IF EXISTS v_ventas_por_producto;
CREATE VIEW v_ventas_por_producto AS
SELECT 
    p.id,
    p.nombre AS producto,
    p.categoria,
    COUNT(DISTINCT ip.pedido_id) AS num_pedidos,
    SUM(ip.cantidad) AS cantidad_vendida,
    SUM(ip.subtotal) AS total_vendido
FROM productos p
INNER JOIN items_pedido ip ON p.id = ip.producto_id
INNER JOIN pedidos ped ON ip.pedido_id = ped.id
WHERE ped.estado = 'ENTREGADO'
GROUP BY p.id, p.nombre, p.categoria
ORDER BY total_vendido DESC;

-- Vista: Resumen de pedidos por cliente
DROP VIEW IF EXISTS v_pedidos_por_cliente;
CREATE VIEW v_pedidos_por_cliente AS
SELECT 
    c.id,
    c.nombre_completo AS cliente,
    c.email,
    COUNT(p.id) AS total_pedidos,
    SUM(CASE WHEN p.estado = 'ENTREGADO' THEN 1 ELSE 0 END) AS pedidos_entregados,
    SUM(CASE WHEN p.estado = 'ENTREGADO' THEN p.total ELSE 0 END) AS total_gastado
FROM clientes c
LEFT JOIN pedidos p ON c.id = p.cliente_id
GROUP BY c.id, c.nombre_completo, c.email
ORDER BY total_gastado DESC;

-- Vista: Estadísticas diarias de ventas
DROP VIEW IF EXISTS v_ventas_diarias;
CREATE VIEW v_ventas_diarias AS
SELECT 
    DATE(fecha_pedido) AS fecha,
    COUNT(*) AS num_pedidos,
    SUM(total) AS ventas_dia,
    AVG(total) AS ticket_promedio,
    SUM(CASE WHEN estado = 'ENTREGADO' THEN total ELSE 0 END) AS ventas_confirmadas
FROM pedidos
GROUP BY DATE(fecha_pedido)
ORDER BY fecha DESC;

-- =====================================================
-- PROCEDIMIENTOS ALMACENADOS
-- =====================================================

DROP PROCEDURE IF EXISTS sp_calcular_totales_pedido;
DROP PROCEDURE IF EXISTS sp_estadisticas_ventas;

DELIMITER //

-- Procedimiento: Calcular totales de un pedido
CREATE PROCEDURE sp_calcular_totales_pedido(IN p_pedido_id BIGINT)
BEGIN
    DECLARE v_subtotal DECIMAL(10,2);
    DECLARE v_igv DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);
    
    -- Calcular subtotal de items
    SELECT COALESCE(SUM(subtotal), 0) INTO v_subtotal
    FROM items_pedido
    WHERE pedido_id = p_pedido_id;
    
    -- Calcular IGV (18%)
    SET v_igv = v_subtotal * 0.18;
    
    -- Calcular total
    SET v_total = v_subtotal + v_igv;
    
    -- Actualizar pedido
    UPDATE pedidos
    SET subtotal = v_subtotal,
        igv = v_igv,
        total = v_total,
        fecha_actualizacion = NOW()
    WHERE id = p_pedido_id;
END //

-- Procedimiento: Obtener estadísticas de ventas
CREATE PROCEDURE sp_estadisticas_ventas(
    IN p_fecha_inicio DATETIME,
    IN p_fecha_fin DATETIME
)
BEGIN
    SELECT 
        COUNT(*) AS total_pedidos,
        SUM(CASE WHEN estado = 'PENDIENTE' THEN 1 ELSE 0 END) AS pendientes,
        SUM(CASE WHEN estado = 'CONFIRMADO' THEN 1 ELSE 0 END) AS confirmados,
        SUM(CASE WHEN estado = 'PREPARANDO' THEN 1 ELSE 0 END) AS en_preparacion,
        SUM(CASE WHEN estado = 'ENVIADO' THEN 1 ELSE 0 END) AS en_camino,
        SUM(CASE WHEN estado = 'ENTREGADO' THEN 1 ELSE 0 END) AS entregados,
        SUM(CASE WHEN estado = 'CANCELADO' THEN 1 ELSE 0 END) AS cancelados,
        SUM(CASE WHEN estado = 'ENTREGADO' THEN total ELSE 0 END) AS ventas_totales,
        AVG(CASE WHEN estado = 'ENTREGADO' THEN total ELSE NULL END) AS ticket_promedio
    FROM pedidos
    WHERE fecha_pedido BETWEEN p_fecha_inicio AND p_fecha_fin;
END //

DELIMITER ;

-- =====================================================
-- VERIFICACIÓN
-- =====================================================

-- Verificar que las tablas se crearon correctamente
SHOW TABLES LIKE '%pedido%';

-- Verificar estructura de la tabla pedidos
DESCRIBE pedidos;

-- Verificar estructura de la tabla items_pedido
DESCRIBE items_pedido;

-- Contar registros
SELECT 'Pedidos creados:' AS Info, COUNT(*) AS Total FROM pedidos;
SELECT 'Items creados:' AS Info, COUNT(*) AS Total FROM items_pedido;

-- =====================================================
-- NOTAS IMPORTANTES
-- =====================================================

-- 1. Este script usa la base de datos existente: juledtoy_bds
-- 2. Asume que existen las tablas 'clientes' y 'productos' con estructura compatible
-- 3. Los datos de ejemplo usan cliente_id = 1 (ajustar según sea necesario)
-- 4. Los estados válidos son: PENDIENTE, CONFIRMADO, PREPARANDO, ENVIADO, ENTREGADO, CANCELADO
-- 5. Para ejecutar: mysql -h 45.79.40.132 -u juledtoy_adminbd -p juledtoy_bds < database_pedidos_mysql.sql

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
