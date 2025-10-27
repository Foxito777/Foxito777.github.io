package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.servicios.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/backoffice/admin/reportes")
public class BackofficeReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping
    public String mostrarReportes(Model model) {
        // Cargar resumen del dashboard
        Map<String, Object> resumen = reporteService.obtenerResumenDashboard();
        model.addAttribute("resumen", resumen);
        
        return "backoffice/reportes";
    }

    @GetMapping("/ventas")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerReporteVentas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        // Si no se especifican fechas, usar últimos 30 días
        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.now().minusDays(30);
        }
        if (fechaFin == null) {
            fechaFin = LocalDateTime.now();
        }

        Map<String, Object> estadisticas = reporteService.obtenerEstadisticasVentas(fechaInicio, fechaFin);
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/productos-mas-vendidos")
    @ResponseBody
    public ResponseEntity<?> obtenerProductosMasVendidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "10") int limite) {
        
        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.now().minusDays(30);
        }
        if (fechaFin == null) {
            fechaFin = LocalDateTime.now();
        }

        var productos = reporteService.obtenerProductosMasVendidos(fechaInicio, fechaFin, limite);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/ventas-por-dia")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerVentasPorDia(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.now().minusDays(30);
        }
        if (fechaFin == null) {
            fechaFin = LocalDateTime.now();
        }

        Map<String, Object> ventasPorDia = reporteService.obtenerVentasPorDia(fechaInicio, fechaFin);
        return ResponseEntity.ok(ventasPorDia);
    }

    @GetMapping("/dashboard")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerDashboard() {
        Map<String, Object> resumen = reporteService.obtenerResumenDashboard();
        return ResponseEntity.ok(resumen);
    }
}
