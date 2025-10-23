param(
  [Parameter(Mandatory=$false)]
  [string]$DbHost = "127.0.0.1",
  [Parameter(Mandatory=$false)]
  [int]$DbPort = 3306,
  [Parameter(Mandatory=$true)]
  [string]$DbName,
  [Parameter(Mandatory=$true)]
  [string]$DbUser,
  [Parameter(Mandatory=$false)]
  [string]$DbPassword,
  [Parameter(Mandatory=$false)]
  [ValidateSet('create','update','none')]
  [string]$DdlAuto = 'update',
  [Parameter(Mandatory=$false)]
  [switch]$UseJar,
  [Parameter(Mandatory=$false)]
  [int]$Port = 8080
)

if (-not $DbPassword) {
  $sec = Read-Host -AsSecureString 'DATABASE_PASSWORD (input oculto)'
  $bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($sec)
  $DbPassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto($bstr)
}

$env:DATABASE_URL = "jdbc:mysql://$DbHost:$DbPort/$DbName?sslMode=PREFERRED&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8"
$env:DATABASE_USERNAME = $DbUser
$env:DATABASE_PASSWORD = $DbPassword
$env:DDL_AUTO = $DdlAuto
$env:SPRING_PROFILES_ACTIVE = 'production'

Write-Host "\nBase de datos:" -ForegroundColor Cyan
Write-Host "  URL:      $($env:DATABASE_URL)"
Write-Host "  USER:     $($env:DATABASE_USERNAME)"
Write-Host "  DDL_AUTO: $($env:DDL_AUTO)"
Write-Host "  Perfil:   $($env:SPRING_PROFILES_ACTIVE)\n"

if ($UseJar) {
  $jar = Join-Path $PSScriptRoot '..' 'target' 'Juledtoys-0.0.1-SNAPSHOT.jar'
  if (-not (Test-Path $jar)) {
    Write-Host "Empaquetando JAR (mvnw package -DskipTests)" -ForegroundColor Yellow
    & (Join-Path $PSScriptRoot '..' 'mvnw.cmd') -DskipTests package
  }
  Write-Host "Arrancando JAR con perfil production en puerto $Port" -ForegroundColor Green
  & java -Dserver.port=$Port -jar (Join-Path $PSScriptRoot '..' 'target' 'Juledtoys-0.0.1-SNAPSHOT.jar')
} else {
  Write-Host "Arrancando con Maven (spring-boot:run)" -ForegroundColor Green
  & (Join-Path $PSScriptRoot '..' 'mvnw.cmd') spring-boot:run -Dspring-boot.run.profiles=production -DskipTests
}
