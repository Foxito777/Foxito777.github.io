# 🌐 Guía para Conectar a MySQL en Servidor de Hosting

## 📋 Información que necesitas de tu proveedor de hosting

Antes de configurar la aplicación, necesitas obtener de tu proveedor de hosting:

### 🔧 Datos de Conexión Requeridos
1. **Host/Servidor**: La dirección del servidor MySQL (ej: `mysql123.hostinger.com`)
2. **Puerto**: Generalmente 3306, pero puede variar
3. **Nombre de la base de datos**: El nombre de tu base de datos
4. **Usuario**: Tu usuario de MySQL
5. **Contraseña**: Tu contraseña de MySQL
6. **Certificados SSL**: Si el hosting requiere SSL (común en hosting compartido)

## 🏢 Configuración por Proveedor de Hosting

### Hostinger
```properties
spring.datasource.url=jdbc:mysql://mysql.hostinger.com:3306/u123456789_juledtoys?useSSL=true&requireSSL=true&serverTimezone=UTC
spring.datasource.username=u123456789_admin
spring.datasource.password=TuContraseñaSegura123
```

### cPanel/Hosting Compartido
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cpanel_usuario_juledtoys?useSSL=false&serverTimezone=UTC
spring.datasource.username=cpanel_usuario
spring.datasource.password=TuContraseña123
```

### DigitalOcean Managed Database
```properties
spring.datasource.url=jdbc:mysql://db-mysql-nyc1-12345-do-user-123456-0.b.db.ondigitalocean.com:25060/juledtoys?useSSL=true&requireSSL=true&serverTimezone=UTC
spring.datasource.username=doadmin
spring.datasource.password=TuTokenDeAcceso
```

### AWS RDS
```properties
spring.datasource.url=jdbc:mysql://juledtoys.cluster-abc123.us-east-1.rds.amazonaws.com:3306/juledtoys?useSSL=true&requireSSL=true&serverTimezone=UTC
spring.datasource.username=admin
spring.datasource.password=TuContraseñaRDS
```

### Google Cloud SQL
```properties
spring.datasource.url=jdbc:mysql://google/juledtoys?cloudSqlInstance=tu-proyecto:us-central1:tu-instancia&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=true
spring.datasource.username=root
spring.datasource.password=TuContraseñaCloudSQL
```

## ⚙️ Pasos para Configurar

### 1. Obtener datos de tu hosting
Busca en el panel de control de tu hosting:
- **cPanel**: Sección "Bases de datos MySQL"
- **Hostinger**: Panel hPanel → "Bases de datos"
- **Hosting compartido**: Busca "MySQL" o "Bases de datos"

### 2. Editar archivo de configuración
Abre el archivo `src/main/resources/application-hosting.properties` y reemplaza:

```properties
# ANTES (datos de ejemplo)
spring.datasource.url=jdbc:mysql://tu-servidor-hosting.com:3306/tu_base_datos?useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=tu_usuario_mysql
spring.datasource.password=tu_contraseña_mysql

# DESPUÉS (con tus datos reales)
spring.datasource.url=jdbc:mysql://mysql.tuhosting.com:3306/tu_base_datos_real?useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=tu_usuario_real
spring.datasource.password=tu_contraseña_real
```

### 3. Ejecutar la aplicación
```bash
# Modo desarrollo (Windows)
mvn spring-boot:run -Dspring-boot.run.profiles=hosting

# Generar JAR para producción
mvn clean package
java -jar -Dspring.profiles.active=hosting target/Juledtoys-0.0.1-SNAPSHOT.jar
```

## 🔐 Configuraciones de Seguridad Adicionales

### Para hosting con SSL obligatorio
```properties
spring.datasource.url=jdbc:mysql://host:3306/db?useSSL=true&requireSSL=true&verifyServerCertificate=false&serverTimezone=UTC
```

### Para hosting sin SSL (menos seguro)
```properties
spring.datasource.url=jdbc:mysql://host:3306/db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

### Configuración con certificado específico
```properties
spring.datasource.url=jdbc:mysql://host:3306/db?useSSL=true&trustCertificateKeyStoreUrl=file:path/to/truststore&trustCertificateKeyStorePassword=password
```

## 🚨 Problemas Comunes y Soluciones

### Error: "Connection refused"
- ✅ Verifica el host y puerto
- ✅ Confirma que el servidor MySQL esté activo
- ✅ Revisa las reglas de firewall del hosting

### Error: "Access denied"
- ✅ Verifica usuario y contraseña
- ✅ Confirma que el usuario tenga permisos en la base de datos
- ✅ Revisa si el hosting requiere IP específica

### Error: "SSL required"
- ✅ Agrega `useSSL=true&requireSSL=true` a la URL
- ✅ Verifica certificados SSL
- ✅ Contacta al soporte de tu hosting

### Error: "Database does not exist"
- ✅ Crea la base de datos en el panel de hosting
- ✅ Verifica el nombre exacto de la base de datos
- ✅ Usa `spring.jpa.hibernate.ddl-auto=create` solo la primera vez

## 📁 Estructura de Archivos de Configuración Final

```
src/main/resources/
├── application.properties          # Configuración por defecto (H2)
├── application-dev.properties      # Perfil de desarrollo (H2)
├── application-mysql.properties    # Perfil MySQL local
├── application-hosting.properties  # Perfil MySQL hosting remoto
```

## 🎯 Variables de Entorno (Recomendado para Producción)

En lugar de hardcodear las credenciales, usa variables de entorno:

```properties
spring.datasource.url=${DATABASE_URL:jdbc:mysql://localhost:3306/juledtoys}
spring.datasource.username=${DATABASE_USERNAME:root}
spring.datasource.password=${DATABASE_PASSWORD:}
```

Luego configura las variables en tu sistema:
```bash
export DATABASE_URL="jdbc:mysql://tu-hosting.com:3306/tu_db?useSSL=true"
export DATABASE_USERNAME="tu_usuario"
export DATABASE_PASSWORD="tu_contraseña"
```

## 📞 Soporte

Si tienes problemas específicos con tu hosting:

1. **Revisa la documentación** de tu proveedor de hosting
2. **Contacta al soporte técnico** con los detalles del error
3. **Verifica los logs** de tu aplicación para errores específicos
4. **Prueba la conexión** usando herramientas como MySQL Workbench primero

---

¡Tu aplicación Juledtoys estará lista para producción con MySQL hosting! 🚀