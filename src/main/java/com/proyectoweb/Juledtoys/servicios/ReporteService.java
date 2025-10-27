package com.proyectoweb.Juledtoys.servicios;

import com.proyectoweb.Juledtoys.repositorios.PedidoRepository;
import com.proyectoweb.Juledtoys.repositorios.ItemPedidoRepository;
import com.proyectoweb.Juledtoys.entidades.EstadoPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReporteService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    /**
     * Obtiene estadísticas de ventas para un período
     */
    public Map<String, Object> obtenerEstadisticasVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Map<String, Object> estadisticas = new HashMap<>();

        // Ventas totales
        Double ventasTotales = pedidoRepository.calcularVentasTotales(fechaInicio, fechaFin);
        estadisticas.put("ventasTotales", ventasTotales != null ? ventasTotales : 0.0);

        // Número de pedidos
        var pedidos = pedidoRepository.findByFechaPedidoBetween(fechaInicio, fechaFin);
        estadisticas.put("numeroPedidos", pedidos.size());

        // Ticket promedio
        double ticketPromedio = pedidos.isEmpty() ? 0.0 : 
            ventasTotales / pedidos.size();
        estadisticas.put("ticketPromedio", 
            BigDecimal.valueOf(ticketPromedio).setScale(2, RoundingMode.HALF_UP).doubleValue());

        // Pedidos por estado
        Map<String, Long> pedidosPorEstado = new HashMap<>();
        pedidosPorEstado.put("PENDIENTE", pedidoRepository.countByEstado(EstadoPedido.PENDIENTE));
        pedidosPorEstado.put("CONFIRMADO", pedidoRepository.countByEstado(EstadoPedido.CONFIRMADO));
        pedidosPorEstado.put("EN_PREPARACION", pedidoRepository.countByEstado(EstadoPedido.EN_PREPARACION));
        pedidosPorEstado.put("EN_CAMINO", pedidoRepository.countByEstado(EstadoPedido.EN_CAMINO));
        pedidosPorEstado.put("ENTREGADO", pedidoRepository.countByEstado(EstadoPedido.ENTREGADO));
        pedidosPorEstado.put("CANCELADO", pedidoRepository.countByEstado(EstadoPedido.CANCELADO));
        estadisticas.put("pedidosPorEstado", pedidosPorEstado);

        return estadisticas;
    }

    /**
     * Obtiene los productos más vendidos
     */
    public List<Map<String, Object>> obtenerProductosMasVendidos(LocalDateTime fechaInicio, 
                                                                   LocalDateTime fechaFin, 
                                                                   int limite) {
        List<Object[]> resultados = itemPedidoRepository.findProductosMasVendidos(fechaInicio, fechaFin);
        List<Map<String, Object>> productos = new ArrayList<>();

        int count = 0;
        for (Object[] resultado : resultados) {
            if (count >= limite) break;
            
            Map<String, Object> producto = new HashMap<>();
            producto.put("nombre", resultado[0]);
            producto.put("cantidadVendida", resultado[1]);
            productos.add(producto);
            count++;
        }

        return productos;
    }

    /**
     * Obtiene datos para gráfica de ventas por día
     */
    public Map<String, Object> obtenerVentasPorDia(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        var pedidos = pedidoRepository.findByEstadoAndFechaPedidoBetween(
            EstadoPedido.ENTREGADO, fechaInicio, fechaFin);

        Map<String, Double> ventasPorDia = new TreeMap<>();
        
        for (var pedido : pedidos) {
            String fecha = pedido.getFechaPedido().toLocalDate().toString();
            ventasPorDia.merge(fecha, pedido.getTotal().doubleValue(), Double::sum);
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("fechas", new ArrayList<>(ventasPorDia.keySet()));
        resultado.put("ventas", new ArrayList<>(ventasPorDia.values()));

        return resultado;
    }

    /**
     * Resumen del dashboard
     */
    public Map<String, Object> obtenerResumenDashboard() {
        LocalDateTime hoy = LocalDateTime.now();
        LocalDateTime hace30Dias = hoy.minusDays(30);

        Map<String, Object> resumen = new HashMap<>();

        // Estadísticas del mes
        resumen.put("ventasMes", obtenerEstadisticasVentas(hace30Dias, hoy));

        // Productos más vendidos del mes
        resumen.put("topProductos", obtenerProductosMasVendidos(hace30Dias, hoy, 10));

        // Ventas por día (últimos 30 días)
        resumen.put("ventasPorDia", obtenerVentasPorDia(hace30Dias, hoy));

        return resumen;
    }
}
