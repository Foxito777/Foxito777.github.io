# Script para corregir estados de pedidos en la base de datos
# Ejecuta un UPDATE para normalizar estados antes de arrancar la aplicación

$server = "45.79.40.132"
$port = "3306"
$database = "juledtoy_bds"
$username = "juledtoy_adminbd"
$password = "Juledbase2025"

Write-Host "Conectando a la base de datos..." -ForegroundColor Yellow

# Crear la conexión usando MySQL Connector
Add-Type -Path "$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar" -ErrorAction SilentlyContinue

try {
    $connectionString = "server=$server;port=$port;uid=$username;pwd=$password;database=$database;SslMode=None;AllowPublicKeyRetrieval=True"
    $connection = New-Object MySql.Data.MySqlClient.MySqlConnection($connectionString)
    $connection.Open()

    Write-Host "Conexión establecida. Actualizando estados..." -ForegroundColor Green

    # Query para actualizar estados inválidos
    $query = "UPDATE pedidos SET estado = 'PENDIENTE' WHERE estado NOT IN ('PENDIENTE', 'CONFIRMADO', 'EN_PREPARACION', 'EN_CAMINO', 'ENTREGADO', 'CANCELADO')"
    
    $command = New-Object MySql.Data.MySqlClient.MySqlCommand($query, $connection)
    $rowsAffected = $command.ExecuteNonQuery()

    Write-Host "Estados actualizados: $rowsAffected registros modificados" -ForegroundColor Green

    # Verificar estados actuales
    $verifyQuery = "SELECT DISTINCT estado, COUNT(*) as cantidad FROM pedidos GROUP BY estado"
    $command = New-Object MySql.Data.MySqlClient.MySqlCommand($verifyQuery, $connection)
    $reader = $command.ExecuteReader()

    Write-Host "`nEstados actuales en la base de datos:" -ForegroundColor Cyan
    while ($reader.Read()) {
        $estado = $reader["estado"]
        $cantidad = $reader["cantidad"]
        Write-Host "  - $estado : $cantidad pedidos" -ForegroundColor White
    }
    $reader.Close()

    $connection.Close()
    Write-Host "`nScript completado exitosamente." -ForegroundColor Green

} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host "`nNo se pudo conectar con MySQL Connector. Usando método alternativo..." -ForegroundColor Yellow
    Write-Host "Ejecuta manualmente esta query en tu cliente MySQL:" -ForegroundColor Cyan
    Write-Host "UPDATE pedidos SET estado = 'PENDIENTE' WHERE estado NOT IN ('PENDIENTE', 'CONFIRMADO', 'EN_PREPARACION', 'EN_CAMINO', 'ENTREGADO', 'CANCELADO');" -ForegroundColor White
}
