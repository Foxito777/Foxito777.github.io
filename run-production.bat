@echo off
echo ========================================
echo Arrancando Juledtoys en modo produccion
echo ========================================
echo.
echo Conectando a MySQL en 45.79.40.132:3306
echo Base de datos: juledtoy_bds
echo Usuario: juledtoy_adminbd
echo DDL: create (primera vez - creara tablas)
echo Puerto: 8081
echo.

java -Dspring.profiles.active=production ^
 -Dspring.datasource.url="jdbc:mysql://45.79.40.132:3306/juledtoy_bds?sslMode=PREFERRED&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8" ^
 -Dspring.datasource.username=juledtoy_adminbd ^
 -Dspring.datasource.password=%DB_PASSWORD% ^
 -Dspring.jpa.hibernate.ddl-auto=create ^
 -Dserver.port=8081 ^
 -jar ".\target\Juledtoys-0.0.1-SNAPSHOT.jar"

echo.
echo ========================================
echo La aplicacion se ha detenido
echo ========================================
pause
