## Documentación de arquitectura y prácticas de ingeniería

Fecha: 27 de octubre de 2025

Este documento resume cómo el proyecto aplica principios arquitectónicos y de ingeniería de software, en particular:

- MVC (Model-View-Controller)
- DAO (Data Access Object) / Repository pattern
- SOLID (ejemplos prácticos)

También se mencionan prácticas relacionadas con TDD y aspectos de seguridad ya considerados en el proyecto y recomendaciones adicionales.

### 1. Resumen ejecutivo

El proyecto está construido con Spring Boot (controladores MVC + Thymeleaf en el front), JPA/Hibernate para persistencia y Spring Security para control de acceso. La estructura de paquetes refleja separación de responsabilidades: `controladores` (web), `servicios` (lógica de negocio), `repositorios` (acceso a datos) y `entidades` (modelos/persistencia). Esto facilita mantener y evolucionar la aplicación respetando principios de diseño.

### 2. MVC (Model-View-Controller)

Cómo se aplica:

- Model: las entidades bajo `src/main/java/.../entidades` (por ejemplo `Producto`, `Pedido`, `ItemPedido`, `Cliente`) representan el dominio y se mapean a la base de datos mediante JPA.
- View: las plantillas Thymeleaf en `src/main/resources/templates` (por ejemplo `detalle-producto.html`, `carrito.html`, `backoffice/pedidos.html`) son las vistas que renderizan datos del modelo; usan fragmentos para reuso (fragments) y formateo seguro (`#numbers`, `#temporals`, escaping automático de Thymeleaf) para reducir riesgo XSS.
- Controller: los controladores en `src/main/java/.../controladores` (por ejemplo `TiendaController`, `CarritoController`, `CheckoutController`, `BackofficePedidoController`) reciben peticiones HTTP, validan/transforman datos y delegan en servicios para la lógica.

Beneficios observados: separación limpia de responsabilidades, facilidad para probar la lógica de negocio (easy to unit test services), y vistas ligeras sin lógica de negocio compleja.

### 3. DAO / Repository pattern

Cómo se aplica:

- Repositorios Spring Data JPA (`src/main/java/.../repositorios`) encapsulan el acceso a datos. Ejemplos: `ProductoRepository`, `PedidoRepository`.
- Los servicios (p. ej. `ProductoService`, `PedidoService`) usan estos repositorios para operaciones transaccionales y de negocio, evitando mezclar SQL/consultas en capas superiores.

Beneficios: abstracción del acceso a datos, pruebas más sencillas (repositorios mockeables), y reutilización/centralización de queries.

### 4. SOLID — ejemplos concretos en el código

Aplicamos al menos los siguientes principios SOLID:

- Single Responsibility Principle (SRP): cada clase tiene una única responsabilidad. Ejemplos:
  - Controladores: gestionan HTTP y delegan lógica.
  - Servicios: contienen la lógica de negocio (p. ej. `CarritoServiceJPA`, `PedidoService`).
  - Repositorios: encargados solo de persistencia.

- Dependency Inversion & Dependency Injection:
  - Uso de inyección de dependencias de Spring (`@Autowired`, inyección por constructor en clases nuevas) para desacoplar implementaciones; los servicios dependen de interfaces/repositorios abstraídos, lo que facilita testing.

- Open/Closed (OCP): muchas extensiones se hacen añadiendo nuevas implementaciones de servicios/repositories o nuevas plantillas sin modificar el núcleo de otros componentes (p. ej. agregar nuevos métodos en `ProductoService` o filtros sin cambiar controladores existentes).

Notas sobre Interface Segregation y Liskov: la estructura actual favorece interfaces y clases con responsabilidades específicas (por ejemplo se pueden definir interfaces para servicios si se necesita mayor modularidad en el futuro).

### 5. TDD (Test-Driven Development)

Estado actual y recomendaciones:

- Hay una estructura de tests (`src/test/java/...`) que permite agregar unit/integration tests; sin embargo, la adopción de TDD como práctica (escribir tests antes de implementar) depende del equipo. Se recomienda:
  - Añadir tests unitarios para servicios críticos (p. ej. `PedidoService`, `CarritoServiceJPA`) cubriendo cálculo de totales y flujo de creación de pedidos.
  - Añadir tests de integración (SpringBootTest) para endpoints importantes como checkout y backoffice.
  - Usar mocks para repositorios cuando se prueban servicios (Mockito o el stack de Spring) y pruebas de comportamiento para flujos E2E.

### 6. Seguridad (prácticas ya aplicadas y recomendaciones)

Prácticas implementadas:

- Spring Security para control de acceso a backoffice y rutas sensibles (configuración en `SecurityConfig`).
- Protección CSRF: formularios de Thymeleaf incluyen token CSRF para POSTs; para peticiones AJAX se deben pasar los headers adecuados (X-CSRF-TOKEN) — revisar `carrito.js`/`detalle-producto.js` si usan fetch/axios.
- Encodings y escape: Thymeleaf por defecto escapa expresiones `${...}` evitando inyección HTML; para casos que usan `th:utext` se debería sanitizar el contenido previamente.
- Validación de entrada: uso de `@Valid` y `BindingResult` en controladores donde corresponde (p. ej. creación/edición de pedidos/usuarios) para validar datos antes de persistir.
- Uso de contraseñas seguras: `PasswordEncoder` (BCrypt) en la configuración de seguridad para almacenar contraseñas (ver `SecurityConfig` y `AuthController`).

Recomendaciones adicionales:

1. Revisar que todas las acciones que cambian estado (eliminar, actualizar estado, crear pedido) requieran un rol adecuado y tengan CSRF habilitado.
2. Para peticiones AJAX incluir CSRF token en headers: leer token desde meta o desde cookie y adjuntarlo.
3. No exponer stack traces en producción — usar páginas de error personalizadas.
4. Revisar las dependencias en `pom.xml` y mantener versiones actualizadas; ejecutar análisis de vulnerabilidades (dependabot o `mvn dependency:check`) periódicamente.
5. Validar tamaños/limites en uploads (si hay imágenes) y sanitizar nombres de archivo.
6. Revisar logs y no escribir datos sensibles en texto claro (passwords, tokens, etc.).

### 7. Decisiones de ingeniería y ejemplos prácticos

- Dinero y precisión: se usa BigDecimal para cálculos monetarios (subtotal/igv/total) y se presta atención a NPEs (defensiva en `PedidoService.guardar` para evitar null subtotals antes de sumar).
- Mutación de stock: la reducción de stock se realiza en servicio al confirmar pedido, en una operación transaccional para mantener consistencia.
- Layout y seguridad en vistas: uso de `th:attr` y sustituciones literales para evitar problemas con concatenaciones peligrosas en Thymeleaf.

### 8. Checklist rápido (qué cubrimos)

- [x] MVC: controladores, servicios, entidades, vistas.
- [x] DAO/Repository: Spring Data JPA separado de la lógica de negocio.
- [x] SOLID: evidencia práctica (SRP, DI/IoC, OCP) en la organización del código.
- [ ] TDD: estructura de tests presente; recomendamos aumentar cobertura y practicar TDD en nuevos features.
- [x] Seguridad: Spring Security, CSRF en formularios, escape de vistas, validaciones.

### 9. Siguientes pasos recomendados (priorizados)

1. Añadir tests unitarios para `PedidoService` y `CarritoServiceJPA` (cobertura de cálculos y flujos críticos).
2. Revisar `detalle-producto.js` y `carrito.js` para asegurar inclusión del token CSRF en peticiones AJAX.
3. Configurar scan automático de dependencias y crear pipeline CI que ejecute tests y análisis de seguridad.
4. Añadir un documento de políticas de seguridad y manejo de secretos (no en repo) y usar variables de entorno/personal access token para despliegues.

### 10. Librerías de apoyo incorporadas

Se han añadido las siguientes librerías que aportan utilidades y funcionalidad adicional:

- Google Guava (colecciones, caching, utilidades de hashing y strings) — útil para operaciones in-memory eficientes.
- Apache POI (poi, poi-ooxml) — lectura y escritura de documentos MS Office (XLS/XLSX/Word), útil para exportes e informes.
- Apache Commons Lang & Commons IO — utilidades de manipulación de strings, objetos y manejo de I/O.
- Logback (configuración mediante `logback-spring.xml`) — control centralizado de logging, con salida a consola y archivo rotativo.

Seguridad y consideraciones al usar estas librerías:

1. Validar y sanitizar cualquier entrada que se use para generar documentos con POI (por ejemplo, nombres de archivos, contenido) para evitar inyección de contenido o información sensible accidental.
2. Para archivos generados por usuarios (uploads) usar límites de tamaño, comprobación de tipo MIME y escanear por virus si aplica antes de procesarlos con POI.
3. Evitar registrar datos sensibles (PII, números de tarjeta, contraseñas) en logs; configurar `logback-spring.xml` para niveles apropiados y filtros si fuera necesario.
4. Mantener versiones actualizadas: agregar escaneo de dependencias automatizado (dependabot o similar) y parchear vulnerabilidades.
5. Para Guava y Commons, preferir APIs inmutables (por ejemplo ImmutableList) y evitar operaciones con referencias compartidas que causen condiciones de carrera.

Estas librerías se incorporaron al `pom.xml` y se añadió una configuración básica de Logback. Tras la inclusión se ejecutó una build para validar compatibilidad.

---

Si quieres, puedo:

- Crear tests unitarios iniciales para `PedidoService` y `CarritoServiceJPA`.
- Añadir instrucciones rápidas y comandos para ejecutar los tests y el escaneo de dependencias en el README.
- Generar un diagrama simple (texto/ASCII o archivo .png) que muestre paquetes y flujos (Controller -> Service -> Repository -> DB).

Indícame cuál de las acciones prefieres y la implemento en la siguiente iteración.
