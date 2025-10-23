# Configurar acceso remoto MySQL para JuledToys

Este documento explica cómo permitir acceso remoto para la app y otorgar permisos al usuario `juledtoy_adminbd`.

## 1) Editar bind-address (permitir conexiones externas)

- Linux (Ubuntu/Debian):
  - Archivo: `/etc/mysql/mysql.conf.d/mysqld.cnf` o `/etc/my.cnf`
  - Buscar/editar:
    ```
    bind-address = 0.0.0.0
    ```
  - Reiniciar MySQL:
    ```bash
    sudo systemctl restart mysql
    # o
    sudo systemctl restart mysqld
    ```

- Windows Server:
  - Archivo de configuración de MySQL (my.ini), típicamente en el directorio de instalación.
  - Establecer `bind-address=0.0.0.0` y reiniciar el servicio MySQL desde “Servicios”.

## 2) Otorgar privilegios al usuario de la app

Ejecutar en el servidor MySQL (como root/admin), por ejemplo con `mysql -u root -p`:

```sql
SOURCE /ruta/al/repo/scripts/mysql_remote_setup.sql;
```

O copiar y pegar los comandos:
```sql
CREATE DATABASE IF NOT EXISTS juledtoy_bds
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'juledtoy_adminbd'@'%' IDENTIFIED BY 'Juledbase2025';
ALTER USER 'juledtoy_adminbd'@'%'
  IDENTIFIED WITH mysql_native_password BY 'Juledbase2025';
GRANT ALL PRIVILEGES ON juledtoy_bds.* TO 'juledtoy_adminbd'@'%';
FLUSH PRIVILEGES;
```

## 3) Abrir el puerto 3306 en firewall (si aplica)

- Linux (UFW):
  ```bash
  sudo ufw allow 3306/tcp
  sudo ufw reload
  ```
- Windows Firewall: crear regla entrante para TCP 3306.

## 4) Probar conexión remota

Desde tu PC (si tienes cliente MySQL):
```bash
mysql -h 45.79.40.132 -P 3306 -u juledtoy_adminbd -p
# contraseña: Juledbase2025
```

## 5) Arrancar la aplicación

- VS Code: Run and Debug → `Run JuledtoysApplication (8080)`
- O desde terminal Windows:
  ```bat
  start-server-8080.bat
  ```

Si la conexión es exitosa, la app quedará estable en `http://localhost:8080/`.