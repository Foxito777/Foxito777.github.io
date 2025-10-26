# Juledtoys — Instrucciones de uso y despliegue

Este repositorio contiene la aplicación Spring Boot "Juledtoys".

Contenido y objetivo
- Código fuente (carpeta `src/`).
- El artefacto compilado (`target/Juledtoys-0.0.1-SNAPSHOT.jar`) no está versionado por defecto.

Opciones para obtener un artefacto funcional y desplegarlo:

1) Release en GitHub (recomendado)
- Compila localmente:
  ```powershell
  .\mvnw -DskipTests clean package
  ```
- El JAR quedará en `target/Juledtoys-0.0.1-SNAPSHOT.jar`.
- En GitHub crea una Release y sube el JAR como asset.

2) GitHub Actions (automático)
- Si creas una etiqueta (tag) que empiece por `v` (ej. `v1.0.0`) y la empujas al repo:
  ```powershell
  git tag v1.0.0
  git push origin v1.0.0
  ```
- La acción `.github/workflows/release.yml` compilará el proyecto y creará una Release con el JAR como asset.

3) Docker
- Genera el JAR localmente con `mvnw package` y luego construye la imagen Docker:
  ```powershell
  docker build -t juledtoys:latest .
  docker run -p 8080:8080 juledtoys:latest
  ```

4) Git LFS (si necesitas versionar binarios) — no configurado por defecto
- Instala y configura `git lfs` si decides versionar los JARs y/o assets pesados.

Ejecutar localmente (sin Docker):
```powershell
java -jar .\target\Juledtoys-0.0.1-SNAPSHOT.jar
# luego abre: http://localhost:8080
```

Notas finales
- `target/` está ignorado por `.gitignore` para evitar subir binarios.
- He añadido un workflow de GitHub Actions que crea Releases cuando se pulsa un tag `v*`.
# Foxito777.github.io
Plataforma Web JuledToys publicado en GitHub pages
