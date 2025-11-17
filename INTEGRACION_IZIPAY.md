# Integración Izipay - Guía de Uso

## ✅ Configuración Completada

Se ha integrado exitosamente el sistema de pago de **Izipay** (pasarela de pagos peruana) en Juledtoys.

### Componentes Creados

#### Backend (Java/Spring Boot)
1. **IzipayConfig.java** - Gestión de configuración desde properties
2. **IzipayService.java** - Integración con API REST de Izipay
3. **IzipayController.java** - Endpoints para pago y webhook
4. **IzipayPaymentRequest.java** - DTO de solicitud
5. **IzipayPaymentResponse.java** - DTO de respuesta
6. **EstadoPedido.PENDIENTE_PAGO** - Nuevo estado para pedidos sin confirmar

#### Frontend (Thymeleaf + JavaScript)
1. **checkout-pago.html** - Página con modal de pago embebido
2. **pago-fallido.html** - Página de error en pago
3. **CheckoutController** modificado - Crea pedidos con estado PENDIENTE_PAGO

#### Configuración
- **application-hosting.properties** - 9 propiedades de Izipay configuradas

---

## 🚀 Flujo de Pago Implementado

1. **Usuario completa checkout** → `/checkout/pagar` (POST)
2. **Se crea pedido** con estado `PENDIENTE_PAGO`
3. **Redirección** → `/checkout/pago` muestra formulario embebido de Izipay
4. **Frontend llama** → `/izipay/create-payment` (POST) para obtener formToken
5. **Modal de Izipay** se muestra con formulario de tarjeta
6. **Usuario ingresa datos** y confirma pago
7. **Izipay procesa** transacción
8. **Webhook** → `/izipay/webhook` (POST) recibe notificación
9. **Estado actualizado**:
   - `PAID` → `CONFIRMADO`
   - `REFUSED`/`ABANDONED` → `CANCELADO`
10. **Usuario redirigido** → `/izipay/return` según resultado

---

## 🔧 Configuración Actual (Modo TEST)

```properties
# Shop ID
izipay.shop.id=35500760

# Credenciales TEST
izipay.test.password=testpassword_VRyqZSAqD8N06SGp5JuKG2g5qkZtczc6O4briPqJcrL
izipay.test.publicKey=testpublickey_GetyhN7S8aHOuixOgQxwCMyQ3y4n66GB1x9ssyYiiOQb
izipay.test.hmacKey=PyD6mA09hD34Rc4GDV7FxnfITvmqW5m4AVem4CueDyIZ

# URLs API
izipay.api.url=https://api.micuentaweb.pe
izipay.js.url=https://static.micuentaweb.pe/static/js/krypton-client/V4.0/stable/kr-payment-form.min.js

# Configuración
izipay.environment=TEST
izipay.currency=PEN
```

---

## 🧪 Cómo Probar la Integración

### Paso 1: Iniciar la Aplicación
```powershell
cd "d:\UTP\Ciclo 6\Curso Integrador I Sistemas Software\Proyecto Final\Juledtoys"
mvn spring-boot:run
```

### Paso 2: Realizar una Compra de Prueba

1. **Navegar** a http://localhost:8080
2. **Iniciar sesión** como cliente (o registrarse)
3. **Agregar productos** al carrito
4. **Ir a Checkout** → `/checkout`
5. **Completar datos** de envío
6. **Hacer clic** en "Pagar" o "Finalizar Compra"

### Paso 3: Usar Tarjeta de Prueba

En el formulario de Izipay que aparece, usa estas tarjetas de TEST:

#### ✅ Pago Exitoso
```
Número: 4970 1000 0000 0001
CVV: 123
Fecha: Cualquier fecha futura (ej: 12/25)
Nombre: TEST USER
```

#### ❌ Pago Rechazado
```
Número: 4970 1000 0000 0028
CVV: 123
Fecha: Cualquier fecha futura
Nombre: TEST USER
```

### Paso 4: Verificar el Resultado

1. **Pago exitoso**:
   - Redirección a `/pago-exitoso`
   - Estado del pedido: `CONFIRMADO`
   - Email de confirmación (si está configurado)

2. **Pago fallido**:
   - Redirección a `/pago-fallido`
   - Estado del pedido: `CANCELADO`
   - Opción de reintentar

### Paso 5: Verificar en Base de Datos

```sql
-- Ver estado del pedido
SELECT numero_pedido, estado, total, fecha_pedido 
FROM pedidos 
ORDER BY fecha_pedido DESC 
LIMIT 5;
```

---

## 📊 Endpoints Disponibles

### Frontend (GET)
- `/checkout` - Página de checkout
- `/checkout/pago` - Página con modal de pago
- `/pago-exitoso` - Confirmación de pago exitoso
- `/izipay/return` - Retorno después del pago

### API (POST)
- `/izipay/create-payment` - Crear formToken
  ```json
  {
    "pedidoId": 123,
    "amount": 150.00,
    "currency": "PEN",
    "orderId": "PED-20241113-001",
    "customerEmail": "cliente@email.com"
  }
  ```

- `/izipay/webhook` - Recibir notificaciones de Izipay
  ```
  Parámetros:
  - kr-hash: Firma HMAC-SHA-256
  - kr-hash-algorithm: "sha256_hmac"
  - kr-answer: Respuesta en Base64
  ```

---

## 🔐 Seguridad Implementada

1. **Verificación de Firma HMAC-SHA-256** en webhook
2. **Autenticación Basic** con shopId:password para API
3. **Validación de pedido** antes de procesar pago
4. **Encriptación SSL/TLS** en todas las comunicaciones
5. **No se almacenan datos de tarjeta** (PCI-DSS compliant)

---

## 📝 Logs para Debugging

El sistema genera logs con emojis para fácil identificación:

```
💳 [Izipay] FormToken creado para pedido: PED-20241113-001
🔔 [Izipay] Webhook recibido
📦 [Izipay] Pedido: PED-20241113-001, Estado: PAID
✅ [Izipay] Pago confirmado para pedido: PED-20241113-001
❌ [Izipay] Error procesando webhook: ...
```

---

## 🚨 Solución de Problemas

### Error: "No se recibió respuesta del pago"
- Verificar que `kr-answer` está presente en la URL de retorno
- Revisar logs del navegador (F12)

### Error: "Firma inválida"
- Verificar `izipay.test.hmacKey` en properties
- Confirmar que el ambiente es TEST

### Error: "Pedido no encontrado"
- Verificar que el pedido se creó en la BD
- Revisar logs de `CheckoutController`

### Webhook no se ejecuta
- En TEST, Izipay puede NO enviar webhooks automáticamente
- Simular manualmente: POST a `/izipay/webhook` con datos válidos
- Configurar URL pública (ngrok/tunneling) para recibir webhooks reales

---

## 🎯 Próximos Pasos para Producción

### 1. Obtener Credenciales de Producción
Contactar a Izipay para:
- Production Password
- Production Public Key
- Production HMAC Key

### 2. Actualizar Configuración
```properties
# Cambiar a producción
izipay.environment=PRODUCTION

# Agregar credenciales reales
izipay.prod.password=tu_password_produccion
izipay.prod.publicKey=tu_publickey_produccion
izipay.prod.hmacKey=tu_hmackey_produccion
```

### 3. Configurar Webhooks en Izipay
En el backoffice de Izipay:
- **URL IPN**: `https://tudominio.com/izipay/webhook`
- **Método**: POST
- **Formato**: Form URL Encoded

### 4. Configurar URL de Retorno
En `checkout-pago.html`, actualizar:
```javascript
// Cambiar de relativa a absoluta
window.location.href = `https://tudominio.com/izipay/return?kr-answer=...`;
```

### 5. Testing en Producción
- Realizar compra con tarjeta real de bajo monto
- Verificar webhook se recibe correctamente
- Confirmar actualización de estado del pedido

---

## 📞 Soporte

- **Izipay Documentación**: https://secure.micuentaweb.pe/doc/
- **Izipay Soporte**: soporte@izipay.pe
- **Manual Técnico**: Consultar PDF de integración de Izipay

---

## ✨ Características Adicionales Sugeridas

1. **Email de Confirmación**: Enviar correo al confirmar pago
2. **Notificaciones SMS**: Integrar con servicio de SMS
3. **Panel de Administración**: Ver transacciones y estados
4. **Reintento de Pago**: Permitir reintentar pago en pedidos cancelados
5. **Múltiples Métodos de Pago**: Agregar transferencia bancaria, billeteras digitales
6. **Descuentos y Cupones**: Aplicar códigos promocionales antes del pago

---

*Integración completada el 13 de Noviembre, 2024*
*Desarrollado para Juledtoys - Curso Integrador I Sistemas Software*
