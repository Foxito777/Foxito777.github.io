@echo off
echo ========================================
echo Recompilando y ejecutando Juledtoys
echo ========================================
echo.

REM Detener cualquier proceso Java existente (opcional, descomentar si es necesario)
REM taskkill /F /IM java.exe 2>nul

echo [1/3] Limpiando proyecto anterior...
call mvnw.cmd clean

echo.
echo [2/3] Compilando proyecto con Maven...
call mvnw.cmd package -DskipTests

echo.
echo [3/3] Iniciando aplicacion en modo produccion...
echo.
echo Conectando a MySQL en 45.79.40.132:3306
echo Base de datos: juledtoy_bds
echo Usuario: juledtoy_adminbd
echo DDL: update (mantiene datos existentes)
echo Puerto: 8081
echo.

set DB_PASSWORD=Juledbase2025

java ^
-Dspring.profiles.active=production ^
-Dspring.datasource.url=jdbc:mysql://45.79.40.132:3306/juledtoy_bds?sslMode=PREFERRED^&allowPublicKeyRetrieval=true^&serverTimezone=UTC^&useUnicode=true^&characterEncoding=UTF-8 ^
-Dspring.datasource.username=juledtoy_adminbd ^
-Dspring.datasource.password=%DB_PASSWORD% ^
-Dspring.jpa.hibernate.ddl-auto=update ^
-Dserver.port=8081 ^
-jar target\Juledtoys-0.0.1-SNAPSHOT.jar

echo.
echo ========================================
echo La aplicacion se ha detenido
echo ========================================
pause
