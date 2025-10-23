# Script para iniciar Juledtoys en Windows
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  JULEDTOYS - Iniciando Servidor" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

# Configurar variables de entorno
$env:DATABASE_URL = "jdbc:mysql://45.79.40.132:3306/juledtoy_bds?sslMode=PREFERRED&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci"
$env:DATABASE_USERNAME = "juledtoy_adminbd"
$env:DATABASE_PASSWORD = "Juledbase2025"

Write-Host "[INFO] Configuración:" -ForegroundColor Yellow
Write-Host "  • Puerto: 8081" -ForegroundColor White
Write-Host "  • Base de datos: 45.79.40.132:3306/juledtoy_bds" -ForegroundColor White
Write-Host "  • Usuario DB: juledtoy_adminbd`n" -ForegroundColor White

Write-Host "[INFO] Iniciando aplicación..." -ForegroundColor Yellow
Write-Host "       Presiona Ctrl+C para detener el servidor`n" -ForegroundColor Gray

# Iniciar la aplicación
java -jar .\target\Juledtoys-0.0.1-SNAPSHOT.jar --server.port=8081

Write-Host "`n[INFO] Servidor detenido." -ForegroundColor Yellow
