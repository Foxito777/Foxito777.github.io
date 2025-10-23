@echo off
echo ========================================
echo Probando conexion MySQL
echo ========================================
echo.
echo Host: 45.79.40.132:3306
echo Database: juledtoy_bds
echo User: juledtoy_adminbd
echo.

REM Compilar una clase de prueba simple con Maven
mvn exec:java -Dexec.mainClass="com.mysql.cj.jdbc.Driver" -Dexec.classpathScope=test ^
-Dexec.args="jdbc:mysql://45.79.40.132:3306/juledtoy_bds?sslMode=PREFERRED&allowPublicKeyRetrieval=true juledtoy_adminbd Juledbase2025"

echo.
echo ========================================
echo Prueba finalizada
echo ========================================
pause
