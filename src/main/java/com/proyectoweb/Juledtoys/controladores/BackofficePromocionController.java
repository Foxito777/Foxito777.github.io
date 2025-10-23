package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.entidades.Promocion;
import com.proyectoweb.Juledtoys.servicios.PromocionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Backoffice (ROLE_ADMIN): Gestión de Promociones
 * Ruta base: /backoffice/admin/promociones
 *
 * Nota: los formularios en Thymeleaf usan th:action para que Spring inyecte el
 * token CSRF.
 */
@Controller
@RequestMapping("/backoffice/admin/promociones")
public class BackofficePromocionController {

    private final PromocionService promocionService;

    public BackofficePromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    // -----------------------------------------------------------------------
    // Dashboard + listado
    // -----------------------------------------------------------------------
    @GetMapping
    public String index(Model model) {
        model.addAttribute("promociones", promocionService.obtenerTodas());

        // KPIs
        model.addAttribute("totalPromociones", promocionService.contarTotal());
        model.addAttribute("promocionesActivas", promocionService.contarActivas());
        model.addAttribute("promocionesProgramadas", promocionService.contarProgramadas());
        model.addAttribute("promocionesFinalizadas", promocionService.contarFinalizadas());

        // Form "crear" por defecto
        model.addAttribute("promocionForm", new Promocion());
        return "backoffice/promociones";
    }

    // -----------------------------------------------------------------------
    // Obtener (para modal de edición via AJAX)
    // -----------------------------------------------------------------------
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Promocion> p = promocionService.obtenerPorId(id);
        return p.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Promoción no encontrada\"}"));
    }

    // -----------------------------------------------------------------------
    // Crear
    // -----------------------------------------------------------------------
    @PostMapping
    public String crear(@ModelAttribute("promocionForm") Promocion form,
            @RequestParam(value = "archivoImagen", required = false) MultipartFile archivoImagen,
            RedirectAttributes ra) {
        try {
            if (archivoImagen != null && !archivoImagen.isEmpty()) {
                String ruta = promocionService.guardarImagen(archivoImagen);
                form.setImagen(ruta);
            }
            saneaCampos(form);
            promocionService.crear(form);
            ra.addFlashAttribute("ok", "Promoción creada correctamente.");
        } catch (IOException e) {
            ra.addFlashAttribute("error", "No se pudo guardar la imagen: " + e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error creando la promoción: " + e.getMessage());
        }
        return "redirect:/backoffice/admin/promociones";
    }

    // -----------------------------------------------------------------------
    // Actualizar
    // -----------------------------------------------------------------------
    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id,
            @ModelAttribute("promocionForm") Promocion form,
            @RequestParam(value = "archivoImagen", required = false) MultipartFile archivoImagen,
            RedirectAttributes ra) {
        try {
            if (archivoImagen != null && !archivoImagen.isEmpty()) {
                String ruta = promocionService.guardarImagen(archivoImagen);
                form.setImagen(ruta);
            } else {
                // Si no se sube imagen nueva, conservar la existente (si la hay)
                promocionService.obtenerPorId(id).ifPresent(pExistente -> {
                    if (StringUtils.hasText(pExistente.getImagen()) && !StringUtils.hasText(form.getImagen())) {
                        form.setImagen(pExistente.getImagen());
                    }
                });
            }

            saneaCampos(form);
            promocionService.actualizar(id, form);
            ra.addFlashAttribute("ok", "Promoción actualizada correctamente.");
        } catch (IOException e) {
            ra.addFlashAttribute("error", "No se pudo guardar la imagen: " + e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error actualizando la promoción: " + e.getMessage());
        }
        return "redirect:/backoffice/admin/promociones";
    }

    // -----------------------------------------------------------------------
    // Eliminar
    // -----------------------------------------------------------------------
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            promocionService.eliminar(id);
            ra.addFlashAttribute("ok", "Promoción eliminada.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/backoffice/admin/promociones";
    }

    // -----------------------------------------------------------------------
    // Reordenamiento (drag & drop) - recibe ids en orden CSV: "5,3,2,1"
    // -----------------------------------------------------------------------
    @PostMapping("/orden")
    public String actualizarOrden(@RequestParam("orden") String ordenCsv,
            RedirectAttributes ra) {
        try {
            List<Long> ids = Arrays.stream(ordenCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

            promocionService.actualizarOrden(ids);
            ra.addFlashAttribute("ok", "Orden actualizado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo actualizar el orden: " + e.getMessage());
        }
        return "redirect:/backoffice/admin/promociones";
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------
    private void saneaCampos(Promocion p) {
        // Evita nulos vacíos que rompan la vista
        if (p.getEtiqueta() == null)
            p.setEtiqueta("");
        if (p.getTitulo() == null)
            p.setTitulo("");
        if (p.getSubtitulo() == null)
            p.setSubtitulo("");
        if (p.getDescripcion() == null)
            p.setDescripcion("");
        if (p.getCtaTexto() == null)
            p.setCtaTexto("Ver más");
        if (p.getCtaUrl() == null || p.getCtaUrl().isBlank())
            p.setCtaUrl("/productos");
        if (p.getPrioridad() == null || p.getPrioridad().isBlank())
            p.setPrioridad("MEDIA");
        if (p.getOrden() <= 0)
            p.setOrden(9999);
    }
}

