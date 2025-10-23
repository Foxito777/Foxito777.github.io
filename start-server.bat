@echo off
echo ========================================
echo   JULEDTOYS - Iniciando Servidor
echo ========================================
echo.
echo [INFO] Puerto: 8081
echo [INFO] Database: 45.79.40.132:3306/juledtoy_bds
echo.
echo [INFO] Presiona Ctrl+C para detener el servidor
echo.

set DATABASE_URL=jdbc:mysql://45.79.40.132:3306/juledtoy_bds?sslMode=PREFERRED^&allowPublicKeyRetrieval=true^&serverTimezone=UTC^&useUnicode=true^&characterEncoding=UTF-8^&connectionCollation=utf8mb4_unicode_ci
set DATABASE_USERNAME=juledtoy_adminbd
set DATABASE_PASSWORD=Juledbase2025

java -jar .\target\Juledtoys-0.0.1-SNAPSHOT.jar --server.port=8081

echo.
echo [INFO] Servidor detenido.
pause
