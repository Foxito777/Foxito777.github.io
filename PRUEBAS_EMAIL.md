# 🔧 GUÍA DE PRUEBAS PARA EMAIL

## Error Actual
```
535 Incorrect authentication data
```

Esto significa que el servidor rechaza las credenciales.

## 🧪 PRUEBAS A REALIZAR

### Opción 1: Usuario sin dominio
Algunos servidores requieren solo el nombre antes del @

**Cambiar en application-hosting.properties:**
```properties
spring.mail.username=juledtoy
```
(en lugar de juledtoy@juledtoys.com)

### Opción 2: Verificar contraseña
La contraseña actual tiene un carácter especial: `Juled2019%`

**Si el % causa problemas, intenta URL-encoding:**
```properties
spring.mail.password=Juled2019%25
```

### Opción 3: Puerto 587 con STARTTLS
```properties
spring.mail.port=587
spring.mail.properties.mail.smtp.ssl.enable=false
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

### Opción 4: Sin SSL Socket Factory
Algunas configuraciones funcionan mejor sin el socketFactory:
```properties
# Comentar estas líneas:
# spring.mail.properties.mail.smtp.socketFactory.port=465
# spring.mail.properties.mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory
# spring.mail.properties.mail.smtp.socketFactory.fallback=false
```

## 📋 CHECKLIST DEL PANEL DE HOSTING

Verifica en tu panel de control:

- [ ] Email `juledtoy@juledtoys.com` está activo
- [ ] No hay límite de envío alcanzado
- [ ] SMTP está habilitado para este email
- [ ] No requiere "contraseña de aplicación" específica
- [ ] El servidor SMTP es realmente `mail.juledtoys.com`
- [ ] Usuario SMTP es `juledtoy@juledtoys.com` o solo `juledtoy`
- [ ] Contraseña es exactamente `Juled2019%`

## 🔍 CÓMO IDENTIFICAR EL PROBLEMA

1. **Entra a tu panel de hosting**
2. **Busca la sección de Email o Correo**
3. **Encuentra el email juledtoy@juledtoys.com**
4. **Busca "Configuración SMTP" o "Configuración de cliente de correo"**
5. **Anota exactamente lo que dice:**
   - Servidor SMTP: _________________
   - Puerto: _________________
   - Usuario: _________________ (¿con @ o sin @?)
   - Requiere SSL/TLS: _________________
   - Tipo de autenticación: _________________

## 💡 SOLUCIÓN TEMPORAL

Mientras se resuelve el problema de SMTP, el sistema ya tiene un **fallback funcional**:

- ✅ Los comprobantes se registran en la consola del servidor
- ✅ La información no se pierde
- ✅ El usuario recibe confirmación
- ✅ Puedes procesar los comprobantes manualmente

**Accede a:**
```
http://localhost:8080/test/email
```

Para hacer pruebas sin afectar el flujo de pagos.
