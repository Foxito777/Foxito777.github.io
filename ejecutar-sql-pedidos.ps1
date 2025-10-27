# =====================================================
# Script PowerShell para ejecutar SQL de Pedidos/Reportes
# JuledToys - MySQL Database Setup
# =====================================================

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "   Configuración de Módulos Pedidos y Reportes" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Configuración de la base de datos
$DB_HOST = "45.79.40.132"
$DB_PORT = "3306"
$DB_NAME = "juledtoy_bds"
$DB_USER = "juledtoy_adminbd"
$DB_PASS = "Juledbase2025"
$SQL_FILE = "database_pedidos_mysql.sql"

# Verificar que existe el archivo SQL
if (-not (Test-Path $SQL_FILE)) {
    Write-Host "ERROR: No se encontró el archivo $SQL_FILE" -ForegroundColor Red
    Write-Host "Asegúrate de estar en el directorio correcto del proyecto" -ForegroundColor Yellow
    exit 1
}

Write-Host "✓ Archivo SQL encontrado: $SQL_FILE" -ForegroundColor Green
Write-Host ""

# Mostrar información de conexión
Write-Host "Configuración de conexión:" -ForegroundColor Yellow
Write-Host "  Host: $DB_HOST" -ForegroundColor White
Write-Host "  Puerto: $DB_PORT" -ForegroundColor White
Write-Host "  Base de datos: $DB_NAME" -ForegroundColor White
Write-Host "  Usuario: $DB_USER" -ForegroundColor White
Write-Host ""

# Preguntar confirmación
Write-Host "¿Desea ejecutar el script SQL en la base de datos? (S/N): " -ForegroundColor Yellow -NoNewline
$confirmacion = Read-Host

if ($confirmacion -ne "S" -and $confirmacion -ne "s") {
    Write-Host "Operación cancelada por el usuario" -ForegroundColor Yellow
    exit 0
}

Write-Host ""
Write-Host "Ejecutando script SQL..." -ForegroundColor Cyan

# Opción 1: Usar mysql.exe (si está instalado)
$mysqlPath = Get-Command mysql -ErrorAction SilentlyContinue

if ($mysqlPath) {
    Write-Host "✓ MySQL CLI encontrado" -ForegroundColor Green
    Write-Host "Ejecutando con mysql.exe..." -ForegroundColor Cyan
    
    # Ejecutar con mysql
    $env:MYSQL_PWD = $DB_PASS
    & mysql -h $DB_HOST -P $DB_PORT -u $DB_USER $DB_NAME -e "source $SQL_FILE"
    Remove-Item Env:\MYSQL_PWD
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✓ Script ejecutado exitosamente" -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "ERROR: Falló la ejecución del script" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "MySQL CLI no encontrado en PATH" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Opciones alternativas:" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1. Instalar MySQL Client:" -ForegroundColor White
    Write-Host "   winget install Oracle.MySQL" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. Usar MySQL Workbench:" -ForegroundColor White
    Write-Host "   - Abre MySQL Workbench" -ForegroundColor Gray
    Write-Host "   - Conéctate al servidor: $DB_HOST" -ForegroundColor Gray
    Write-Host "   - Abre el archivo: $SQL_FILE" -ForegroundColor Gray
    Write-Host "   - Ejecuta el script (Ctrl+Shift+Enter)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "3. Copiar comando para ejecutar manualmente:" -ForegroundColor White
    Write-Host "   mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS $DB_NAME < $SQL_FILE" -ForegroundColor Gray
    Write-Host ""
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "   Configuración completada" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Próximos pasos:" -ForegroundColor Yellow
Write-Host "1. Verifica que las tablas se crearon correctamente" -ForegroundColor White
Write-Host "2. Reinicia la aplicación Spring Boot con perfil MySQL" -ForegroundColor White
Write-Host "3. Accede a: http://localhost:8080/backoffice/admin/pedidos" -ForegroundColor White
Write-Host ""
