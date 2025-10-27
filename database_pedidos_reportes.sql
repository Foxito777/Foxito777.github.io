-- =====================================================
-- SCRIPT SQL PARA MÓDULOS DE PEDIDOS Y REPORTES
-- JuledToys - Sistema de Gestión
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
    INDEX idx_fecha_pedido (fecha_pedido)
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
    INDEX idx_producto (producto_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- DATOS DE EJEMPLO
-- =====================================================

-- Insertar pedidos de ejemplo (asumiendo que existen clientes y productos)
INSERT INTO pedidos (numero_pedido, cliente_id, fecha_pedido, estado, subtotal, igv, total, direccion_envio, telefono_contacto, metodo_pago, observaciones, fecha_entrega_estimada)
VALUES
('PED-20251026-0001', 1, '2025-10-20 10:30:00', 'ENTREGADO', 250.00, 45.00, 295.00, 'Av. Principal 123, Lima', '987654321', 'Tarjeta', 'Entrega en horario de oficina', '2025-10-22 14:00:00'),
('PED-20251026-0002', 2, '2025-10-21 15:45:00', 'EN_CAMINO', 180.00, 32.40, 212.40, 'Jr. Los Rosales 456, San Isidro', '965432178', 'Efectivo', NULL, '2025-10-26 18:00:00'),
('PED-20251026-0003', 1, '2025-10-23 09:15:00', 'EN_PREPARACION', 420.00, 75.60, 495.60, 'Calle Las Flores 789, Miraflores', '912345678', 'Yape/Plin', 'Llamar antes de entregar', '2025-10-27 16:00:00'),
('PED-20251026-0004', 3, '2025-10-24 11:20:00', 'CONFIRMADO', 150.00, 27.00, 177.00, 'Av. Arequipa 321, Lince', '998765432', 'Transferencia', NULL, '2025-10-28 12:00:00'),
('PED-20251026-0005', 2, '2025-10-25 16:30:00', 'PENDIENTE', 320.00, 57.60, 377.60, 'Calle Lima 654, Surco', '987123456', 'Tarjeta', 'Entregar en la tarde', '2025-10-29 15:00:00'),
('PED-20251026-0006', 4, '2025-10-26 08:00:00', 'ENTREGADO', 890.00, 160.20, 1050.20, 'Av. Javier Prado 1234, San Borja', '943218765', 'Efectivo', 'Pedido urgente', '2025-10-26 20:00:00'),
('PED-20251026-0007', 1, '2025-10-19 13:45:00', 'ENTREGADO', 560.00, 100.80, 660.80, 'Av. Principal 123, Lima', '987654321', 'Yape/Plin', NULL, '2025-10-21 10:00:00'),
('PED-20251026-0008', 5, '2025-10-18 10:00:00', 'CANCELADO', 200.00, 36.00, 236.00, 'Jr. Comercio 987, Pueblo Libre', '921345678', 'Tarjeta', 'Cliente canceló', NULL);

-- Insertar items de pedido de ejemplo (asumiendo productos con IDs 1-10)
INSERT INTO items_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal, descuento)
VALUES
-- Pedido 1
(1, 1, 2, 75.00, 150.00, 0.00),
(1, 3, 1, 100.00, 100.00, 0.00),
-- Pedido 2
(2, 2, 3, 60.00, 180.00, 0.00),
-- Pedido 3
(3, 5, 1, 200.00, 200.00, 0.00),
(3, 4, 2, 110.00, 220.00, 0.00),
-- Pedido 4
(4, 6, 2, 75.00, 150.00, 0.00),
-- Pedido 5
(5, 7, 4, 80.00, 320.00, 0.00),
-- Pedido 6
(6, 8, 1, 450.00, 450.00, 0.00),
(6, 9, 2, 220.00, 440.00, 0.00),
-- Pedido 7
(7, 1, 4, 75.00, 300.00, 0.00),
(7, 10, 1, 260.00, 260.00, 0.00),
-- Pedido 8
(8, 2, 2, 100.00, 200.00, 0.00);

-- =====================================================
-- VISTAS ÚTILES PARA REPORTES
-- =====================================================

-- Vista: Resumen de ventas por producto
CREATE OR REPLACE VIEW v_ventas_por_producto AS
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
CREATE OR REPLACE VIEW v_pedidos_por_cliente AS
SELECT 
    c.id,
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    c.email,
    COUNT(p.id) AS total_pedidos,
    SUM(CASE WHEN p.estado = 'ENTREGADO' THEN 1 ELSE 0 END) AS pedidos_entregados,
    SUM(CASE WHEN p.estado = 'ENTREGADO' THEN p.total ELSE 0 END) AS total_gastado
FROM clientes c
LEFT JOIN pedidos p ON c.id = p.cliente_id
GROUP BY c.id, c.nombre, c.apellido, c.email
ORDER BY total_gastado DESC;

-- Vista: Estadísticas diarias de ventas
CREATE OR REPLACE VIEW v_ventas_diarias AS
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
-- PROCEDIMIENTOS ALMACENADOS (OPCIONAL)
-- =====================================================

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
        SUM(CASE WHEN estado = 'EN_PREPARACION' THEN 1 ELSE 0 END) AS en_preparacion,
        SUM(CASE WHEN estado = 'EN_CAMINO' THEN 1 ELSE 0 END) AS en_camino,
        SUM(CASE WHEN estado = 'ENTREGADO' THEN 1 ELSE 0 END) AS entregados,
        SUM(CASE WHEN estado = 'CANCELADO' THEN 1 ELSE 0 END) AS cancelados,
        SUM(CASE WHEN estado = 'ENTREGADO' THEN total ELSE 0 END) AS ventas_totales,
        AVG(CASE WHEN estado = 'ENTREGADO' THEN total ELSE NULL END) AS ticket_promedio
    FROM pedidos
    WHERE fecha_pedido BETWEEN p_fecha_inicio AND p_fecha_fin;
END //

DELIMITER ;

-- =====================================================
-- ÍNDICES ADICIONALES PARA OPTIMIZACIÓN DE REPORTES
-- =====================================================

-- Índice compuesto para consultas de reportes
CREATE INDEX idx_estado_fecha ON pedidos(estado, fecha_pedido);

-- Índice para búsquedas de productos en items
CREATE INDEX idx_producto_cantidad ON items_pedido(producto_id, cantidad);

-- =====================================================
-- COMENTARIOS
-- =====================================================

-- Para usar este script:
-- 1. Asegúrate de que existan las tablas 'clientes' y 'productos'
-- 2. Ejecuta este script en tu base de datos MySQL
-- 3. Verifica que los IDs de clientes y productos en los datos de ejemplo existan
-- 4. Los procedimientos almacenados son opcionales pero útiles para optimización

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
