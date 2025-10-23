package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.entidades.Promocion;
import com.proyectoweb.Juledtoys.servicios.PromocionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * API pública para exponer promociones hacia el frontend.
 * El carrusel de la página de inicio puede consumir este endpoint
 * en lugar de depender directamente del modelo Thymeleaf.
 *
 * Ruta: /api/promociones/activas
 */
@RestController
@CrossOrigin(origins = "*") // Permitir CORS si es necesario
public class PromocionApiController {

    private final PromocionService promocionService;

    public PromocionApiController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    /**
     * Devuelve las promociones destacadas y activas (filtradas por fecha y estado),
     * ordenadas por el campo "orden".
     * Responde con status HTTP adecuados y manejo de errores.
     */
    @GetMapping("/api/promociones/activas")
    public ResponseEntity<List<Promocion>> obtenerPromocionesActivas() {
        try {
            List<Promocion> promociones = promocionService.obtenerPromocionesDestacadasParaHome();
            
            // Log para debugging
            System.out.println("📊 API: Devolviendo " + promociones.size() + " promociones activas");
            
            if (promociones.isEmpty()) {
                // Si no hay promociones, devolver 204 No Content
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.ok(promociones);
            
        } catch (Exception e) {
            // Log del error
            System.err.println("❌ Error al obtener promociones activas: " + e.getMessage());
            e.printStackTrace();
            
            // Devolver error 500
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Endpoint adicional para obtener todas las promociones activas (no solo destacadas)
     */
    @GetMapping("/api/promociones/todas-activas")
    public ResponseEntity<List<Promocion>> obtenerTodasLasPromocionesActivas() {
        try {
            List<Promocion> promociones = promocionService.obtenerActivas();
            
            System.out.println("📊 API: Devolviendo " + promociones.size() + " promociones (todas activas)");
            
            return ResponseEntity.ok(promociones);
            
        } catch (Exception e) {
            System.err.println("❌ Error al obtener todas las promociones activas: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
