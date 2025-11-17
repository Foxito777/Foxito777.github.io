# ============================================================================
# Script de Migracion - Base de Datos
# Elimina IGV y Estado PENDIENTE de los pedidos
# ============================================================================

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "   MIGRACION: Eliminar IGV y Estado PENDIENTE              " -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# Obtener las credenciales de la base de datos
$dbHost = Read-Host "Host de la base de datos (default: localhost)"
if ([string]::IsNullOrWhiteSpace($dbHost)) { $dbHost = "localhost" }

$dbPort = Read-Host "Puerto (default: 3306)"
if ([string]::IsNullOrWhiteSpace($dbPort)) { $dbPort = "3306" }

$dbName = Read-Host "Nombre de la base de datos (default: juledtoys)"
if ([string]::IsNullOrWhiteSpace($dbName)) { $dbName = "juledtoys" }

$dbUser = Read-Host "Usuario de la base de datos"
if ([string]::IsNullOrWhiteSpace($dbUser)) {
    Write-Host "[ERROR] Usuario es requerido" -ForegroundColor Red
    exit 1
}

$dbPass = Read-Host "Contraseña" -AsSecureString
$dbPassPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($dbPass)
)

Write-Host ""
Write-Host "[INFO] Configuracion:" -ForegroundColor Yellow
Write-Host "   Host: $dbHost" -ForegroundColor Gray
Write-Host "   Puerto: $dbPort" -ForegroundColor Gray
Write-Host "   Base de datos: $dbName" -ForegroundColor Gray
Write-Host "   Usuario: $dbUser" -ForegroundColor Gray
Write-Host ""

# Confirmar ejecución
$confirm = Read-Host "Desea continuar con la migracion? (S/N)"
if ($confirm -ne "S" -and $confirm -ne "s") {
    Write-Host "[CANCELADO] Migracion cancelada" -ForegroundColor Red
    exit 0
}

Write-Host ""
Write-Host "[EJECUTANDO] Migracion en progreso..." -ForegroundColor Cyan
Write-Host ""

# Ruta del script SQL
$scriptPath = Join-Path $PSScriptRoot "migration_remove_igv_and_pendiente.sql"

if (-not (Test-Path $scriptPath)) {
    Write-Host "❌ No se encontró el archivo SQL: $scriptPath" -ForegroundColor Red
    exit 1
}

# Ejecutar el script SQL usando mysql
try {
    $mysqlCmd = "mysql -h $dbHost -P $dbPort -u $dbUser -p$dbPassPlain $dbName"
    
    # Verificar si mysql está disponible
    $mysqlPath = Get-Command mysql -ErrorAction SilentlyContinue
    if (-not $mysqlPath) {
        Write-Host "[ERROR] No se encontro el comando 'mysql' en el PATH" -ForegroundColor Red
        Write-Host "   Por favor, instale MySQL Client o agregue MySQL al PATH" -ForegroundColor Yellow
        exit 1
    }

    # Ejecutar el script
    Get-Content $scriptPath | & mysql -h $dbHost -P $dbPort -u $dbUser "-p$dbPassPlain" $dbName

    Write-Host ""
    Write-Host "[EXITO] Migracion completada exitosamente!" -ForegroundColor Green
    Write-Host ""
    Write-Host "[CAMBIOS] Realizados:" -ForegroundColor Yellow
    Write-Host "   - IGV actualizado a 0.00 en todos los pedidos" -ForegroundColor Gray
    Write-Host "   - Total = Subtotal en todos los pedidos" -ForegroundColor Gray
    Write-Host "   - Estados PENDIENTE cambiados a RECIBIDO" -ForegroundColor Gray
    Write-Host ""
    Write-Host "[IMPORTANTE] Recuerde:" -ForegroundColor Cyan
    Write-Host "   - Reiniciar la aplicacion Spring Boot" -ForegroundColor Gray
    Write-Host "   - Verificar que los pedidos se muestren correctamente" -ForegroundColor Gray
    Write-Host ""

} catch {
    Write-Host ""
    Write-Host "[ERROR] Error al ejecutar la migracion:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

# Limpiar la contraseña de la memoria
$dbPassPlain = $null

Write-Host "Presione cualquier tecla para salir..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
