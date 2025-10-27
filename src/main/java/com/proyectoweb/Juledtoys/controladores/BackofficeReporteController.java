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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/backoffice/admin/reportes")
public class BackofficeReporteController {

    @Autowired
    private ReporteService reporteService;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BackofficeReporteController.class);

    @GetMapping
    public String mostrarReportes(Model model) {
        // Cargar resumen del dashboard
        Map<String, Object> resumen = reporteService.obtenerResumenDashboard();
        model.addAttribute("resumen", resumen);
        
        return "backoffice/reportes";
    }

    /**
     * Exporta un archivo Excel con los pedidos entre fechaInicio y fechaFin.
     * Los parámetros deben venir en formato: yyyy-MM-dd'T'HH:mm (por ejemplo: 2025-10-01T00:00)
     */
    @GetMapping(path = "/exportar-pedidos")
    @ResponseBody
    public ResponseEntity<byte[]> exportarPedidos(@RequestParam(name = "inicio") String inicio,
                                                  @RequestParam(name = "fin") String fin) throws IOException {
        logger.info("Petición exportar-pedidos recibida: inicio={}, fin={}", inicio, fin);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime fechaInicio = LocalDateTime.parse(inicio, df);
        LocalDateTime fechaFin = LocalDateTime.parse(fin, df);

        byte[] bytes = reporteService.exportarPedidosExcel(fechaInicio, fechaFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        String filename = String.format("pedidos_%s_a_%s.xlsx", inicio.replace(':', '-'), fin.replace(':', '-'));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        return ResponseEntity.ok().headers(headers).body(bytes);
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
