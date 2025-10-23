@echo off
REM Script para configurar variables de entorno para Juledtoys
REM Ejecuta este archivo antes de ejecutar la aplicación en producción

echo Configurando variables de entorno para Juledtoys...

REM ===================== CONFIGURACIÓN DE BASE DE DATOS =====================
REM Reemplaza estos valores con los datos reales de tu hosting

REM URL completa de conexión a MySQL hosting
set DATABASE_URL=jdbc:mysql://tu-servidor-hosting.com:3306/tu_base_datos?useSSL=true^&requireSSL=true^&serverTimezone=UTC^&allowPublicKeyRetrieval=true^&useUnicode=true^&characterEncoding=UTF-8

REM Credenciales de la base de datos
set DATABASE_USERNAME=tu_usuario_mysql
set DATABASE_PASSWORD=tu_contraseña_mysql

REM ===================== CONFIGURACIÓN OPCIONAL =====================

REM Tamaño del pool de conexiones (opcional)
set DB_POOL_SIZE=5
set DB_POOL_MIN=2

REM Configuración de DDL (update, create, create-drop, validate)
set DDL_AUTO=update

REM Nivel de logging (DEBUG, INFO, WARN, ERROR)
set LOG_LEVEL=INFO

REM Puerto del servidor (opcional)
set PORT=8080

echo Variables de entorno configuradas:
echo DATABASE_URL=%DATABASE_URL%
echo DATABASE_USERNAME=%DATABASE_USERNAME%
echo DATABASE_PASSWORD=[OCULTA]
echo DB_POOL_SIZE=%DB_POOL_SIZE%
echo DDL_AUTO=%DDL_AUTO%
echo LOG_LEVEL=%LOG_LEVEL%
echo PORT=%PORT%

echo.
echo Ahora puedes ejecutar la aplicación con:
echo mvn spring-boot:run -Dspring-boot.run.profiles=production
echo.
echo O generar el JAR y ejecutarlo:
echo mvn clean package
echo java -jar -Dspring.profiles.active=production target/Juledtoys-0.0.1-SNAPSHOT.jar

pause