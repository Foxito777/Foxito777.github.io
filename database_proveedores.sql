-- Script SQL para crear las tablas del módulo de Proveedores
-- Ejecutar en MySQL/MariaDB

-- Tabla de Proveedores
CREATE TABLE IF NOT EXISTS proveedores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    razon_social VARCHAR(200) NOT NULL,
    ruc VARCHAR(11) NOT NULL UNIQUE,
    direccion VARCHAR(200),
    telefono VARCHAR(15),
    email VARCHAR(100),
    persona_contacto VARCHAR(100),
    notas VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ruc (ruc),
    INDEX idx_activo (activo),
    INDEX idx_razon_social (razon_social)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Compras a Proveedores
CREATE TABLE IF NOT EXISTS compras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id BIGINT NOT NULL,
    numero_orden VARCHAR(50) NOT NULL UNIQUE,
    fecha_compra DATE NOT NULL,
    monto_total DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    observaciones VARCHAR(500),
    fecha_recepcion DATE,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE CASCADE,
    INDEX idx_proveedor (proveedor_id),
    INDEX idx_estado (estado),
    INDEX idx_fecha_compra (fecha_compra),
    INDEX idx_numero_orden (numero_orden)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Entregas de Proveedores
CREATE TABLE IF NOT EXISTS entregas_proveedor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id BIGINT NOT NULL,
    numero_guia VARCHAR(50) NOT NULL UNIQUE,
    fecha_entrega DATE NOT NULL,
    transportista VARCHAR(100) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'EN_TRANSITO',
    observaciones VARCHAR(500),
    fecha_recepcion DATE,
    recibido_por VARCHAR(100),
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE CASCADE,
    INDEX idx_proveedor (proveedor_id),
    INDEX idx_estado (estado),
    INDEX idx_fecha_entrega (fecha_entrega),
    INDEX idx_numero_guia (numero_guia)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos de ejemplo (opcional)
INSERT INTO proveedores (razon_social, ruc, direccion, telefono, email, persona_contacto, notas, activo) VALUES
('IMPORTADORA TOYS S.A.C.', '20123456789', 'Av. Industrial 123, Lima', '987654321', 'ventas@importadoratoys.com', 'Juan Pérez', 'Proveedor principal de juguetes', TRUE),
('DISTRIBUIDORA LEGO PERÚ S.A.', '20987654321', 'Jr. Comercio 456, San Isidro', '912345678', 'contacto@legoperu.com', 'María García', 'Distribuidor oficial LEGO', TRUE),
('JUGUETES EXPRESS E.I.R.L.', '20456789123', 'Calle Las Flores 789, Miraflores', '998877665', 'info@juguetesexpress.com', 'Carlos López', 'Entregas rápidas', TRUE);

INSERT INTO compras (proveedor_id, numero_orden, fecha_compra, monto_total, estado, observaciones) VALUES
(1, 'OC-2024-001', '2024-01-15', 15000.00, 'RECIBIDA', 'Compra de sets Minecraft'),
(2, 'OC-2024-002', '2024-02-20', 25000.00, 'PENDIENTE', 'Sets LEGO Star Wars'),
(3, 'OC-2024-003', '2024-03-10', 8500.00, 'RECIBIDA', 'Accesorios varios');

INSERT INTO entregas_proveedor (proveedor_id, numero_guia, fecha_entrega, transportista, estado, recibido_por) VALUES
(1, 'GR-2024-001', '2024-01-18', 'Transportes Rápidos S.A.', 'ENTREGADA', 'Almacén Central'),
(2, 'GR-2024-002', '2024-02-25', 'Courier Express', 'EN_TRANSITO', NULL),
(3, 'GR-2024-003', '2024-03-12', 'Olva Courier', 'ENTREGADA', 'Juan Ramírez');

-- Verificar la creación de las tablas
SELECT 'Tablas creadas exitosamente' AS mensaje;
