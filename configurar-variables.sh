#!/bin/bash

# Script para configurar variables de entorno para Juledtoys
# Ejecuta este archivo antes de ejecutar la aplicación en producción

echo "Configurando variables de entorno para Juledtoys..."

# ===================== CONFIGURACIÓN DE BASE DE DATOS =====================
# Reemplaza estos valores con los datos reales de tu hosting

# URL completa de conexión a MySQL hosting
export PHPMYADMIN_URL="http://45.79.40.132:2082/cpsess2905905576/3rdparty/phpMyAdmin/index.php?route=/database/structure&db=juledtoy_bds"

# URL completa de conexión a MySQL hosting
export DATABASE_URL="jdbc:mysql://tu-servidor-hosting.com:3306/tu_base_datos?useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8"

# Credenciales de la base de datos
export DATABASE_USERNAME="juledtoy_adminbd"
export DATABASE_PASSWORD="Juledbase2025"

# ===================== CONFIGURACIÓN OPCIONAL =====================

# Tamaño del pool de conexiones (opcional)
export DB_POOL_SIZE=5
export DB_POOL_MIN=2

# Configuración de DDL (update, create, create-drop, validate)
export DDL_AUTO="update"

# Nivel de logging (DEBUG, INFO, WARN, ERROR)
export LOG_LEVEL="INFO"

# Puerto del servidor (opcional)
export PORT=8080

echo "Variables de entorno configuradas:"
echo "DATABASE_URL=$DATABASE_URL"
echo "DATABASE_USERNAME=$DATABASE_USERNAME"
echo "DATABASE_PASSWORD=[OCULTA]"
echo "DB_POOL_SIZE=$DB_POOL_SIZE"
echo "DDL_AUTO=$DDL_AUTO"
echo "LOG_LEVEL=$LOG_LEVEL"
echo "PORT=$PORT"

echo ""
echo "Ahora puedes ejecutar la aplicación con:"
echo "mvn spring-boot:run -Dspring-boot.run.profiles=production"
echo ""
echo "O generar el JAR y ejecutarlo:"
echo "mvn clean package"
echo "java -jar -Dspring.profiles.active=ALTER TABLE `usuarios`
  CONVERT TO CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Opcional: refuerza columnas clave (ajusta a tus nombres reales si difieren)
-- ALTER TABLE `usuarios`
--   MODIFY `nombre`    VARCHAR(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
--   MODIFY `apellido`  VARCHAR(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
--   MODIFY `username`  VARCHAR(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
--   MODIFY `email`     VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
--   MODIFY `direccion` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;production target/Juledtoys-0.0.1-SNAPSHOT.jar"