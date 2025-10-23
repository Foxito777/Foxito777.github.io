# Script para iniciar Juledtoys con configuración correcta
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Iniciando Juledtoys en localhost:8081" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Configurar variables de entorno para MySQL remoto y puerto
$env:DATABASE_URL = "jdbc:mysql://45.79.40.132:3306/juledtoy_bds?sslMode=PREFERRED&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci"
$env:DATABASE_USERNAME = "juledtoy_adminbd"
$env:DATABASE_PASSWORD = "Juledbase2025"
$env:SERVER_PORT = "8081"

Write-Host "`n[*] Configuración MySQL:" -ForegroundColor Green
Write-Host "   Host: 45.79.40.132:3306" -ForegroundColor White
Write-Host "   Database: juledtoy_bds" -ForegroundColor White
Write-Host "   Usuario: juledtoy_adminbd" -ForegroundColor White
Write-Host "`n[*] Puerto: 8081" -ForegroundColor Green

# Iniciar aplicación
Write-Host "`n[*] Iniciando Spring Boot..." -ForegroundColor Yellow
& .\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
