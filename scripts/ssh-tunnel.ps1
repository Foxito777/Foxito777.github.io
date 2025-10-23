param(
  [Parameter(Mandatory=$true)]
  [string]$SshUser,
  [Parameter(Mandatory=$false)]
  [string]$SshHost = "45.79.40.132",
  [Parameter(Mandatory=$false)]
  [int]$LocalPort = 3307,
  [Parameter(Mandatory=$false)]
  [int]$RemotePort = 3306
)

Write-Host "Abriendo túnel SSH: localhost:$LocalPort -> $SshHost:$RemotePort (usuario: $SshUser)" -ForegroundColor Cyan

# -N: no ejecutar comando remoto; -L: port forward local
ssh -N -L "$LocalPort:127.0.0.1:$RemotePort" "$SshUser@$SshHost"
