package com.proyectoweb.Juledtoys.servicios;

import com.proyectoweb.Juledtoys.entidades.Promocion;
import com.proyectoweb.Juledtoys.repositorios.PromocionRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para Promociones.
 * Mantiene la misma filosofía que el módulo de Productos (in-memory).
 */
@Service
public class PromocionService {

    private final PromocionRepositoryJPA repository;

    // Carpeta para subir imágenes (estático)
    private static final String STATIC_DIR = "src/main/resources/static";
    private static final String PROMOS_DIR = "/Imagenes/promociones/";

    public PromocionService(PromocionRepositoryJPA repository) {
        this.repository = repository;
    }

    // ---------- Lectura ----------

    public List<Promocion> obtenerTodas() {
        List<Promocion> list = repository.findAll();
        list.sort((a, b) -> Integer.compare(a.getOrden(), b.getOrden()));
        return list;
    }

    public Optional<Promocion> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    public List<Promocion> obtenerActivas() {
        return repository.findActivas();
    }

    /**
     * Lista priorizada de promociones para el carrusel del Home.
     * Criterios: destacadas, activas hoy y ordenadas por "orden".
     */
    public List<Promocion> obtenerPromocionesDestacadasParaHome() {
        return repository.findDestacadas();
    }

    // ---------- Escritura ----------

    public Promocion crear(Promocion p) {
        normalizarCampos(p);
        return repository.save(p);
    }

    public Promocion actualizar(Long id, Promocion p) {
        p.setId(id);
        normalizarCampos(p);
        return repository.save(p);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public void actualizarOrden(List<Long> idsOrdenados) {
        if (idsOrdenados == null)
            idsOrdenados = Collections.emptyList();
        repository.reorder(idsOrdenados);
    }

    // ---------- Contadores para dashboard ----------

    public long contarTotal() {
        return repository.findAll().size();
    }

    public long contarActivas() {
        return repository.countActivas();
    }

    public long contarProgramadas() {
        return repository.countProgramadas();
    }

    public long contarFinalizadas() {
        return repository.countFinalizadas();
    }

    // ---------- Utilidades ----------

    /**
     * Guarda la imagen subida en /static/Imagenes/promociones
     * y devuelve la ruta pública para Thymeleaf: /Imagenes/promociones/archivo.ext
     */
    public String guardarImagen(MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty())
            return null;

        // Asegura la carpeta
        Path dir = Paths.get(STATIC_DIR + PROMOS_DIR);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        // Nombre simple: timestamp + nombre original saneado
        String original = archivo.getOriginalFilename();
        String safe = (original == null) ? "promo" : original.replaceAll("[^a-zA-Z0-9._-]", "_");
        String filename = System.currentTimeMillis() + "_" + safe;

        Path destino = dir.resolve(filename);
        Files.copy(archivo.getInputStream(), destino);

        // Ruta pública para usar en <img th:src="...">
        return PROMOS_DIR + filename;
    }

    /**
     * Aplica defaults y regla simple de estado según fechas.
     */
    private void normalizarCampos(Promocion p) {
        if (p.getOrden() <= 0)
            p.setOrden(9999); // al final por defecto
        if (p.getPrioridad() == null)
            p.setPrioridad("MEDIA");
        if (p.getColorFondo() == null || p.getColorFondo().isBlank())
            p.setColorFondo("#ff6b35");
        if (p.getColorTexto() == null || p.getColorTexto().isBlank())
            p.setColorTexto("#ffffff");

        // Estado por fechas (solo si no viene específicado)
        if (p.getEstado() == null || p.getEstado().isBlank()) {
            LocalDate hoy = LocalDate.now();
            if (p.getInicio() != null && p.getInicio().isAfter(hoy)) {
                p.setEstado("PROGRAMADA");
            } else if (p.getFin() != null && p.getFin().isBefore(hoy)) {
                p.setEstado("FINALIZADA");
            } else {
                p.setEstado("ACTIVA");
            }
        }

        // Normalizar CTA
        if (p.getCtaTexto() == null || p.getCtaTexto().isBlank()) {
            p.setCtaTexto("Ver más");
        }
        if (p.getCtaUrl() == null || p.getCtaUrl().isBlank()) {
            p.setCtaUrl("/productos");
        }
    }
}
