package com.proyectoweb.Juledtoys.servicios;

import com.proyectoweb.Juledtoys.entidades.Pedido;
import com.proyectoweb.Juledtoys.entidades.EstadoPedido;
import com.proyectoweb.Juledtoys.repositorios.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Optional<Pedido> buscarPorNumero(String numeroPedido) {
        return pedidoRepository.findByNumeroPedido(numeroPedido);
    }

    public Pedido guardar(Pedido pedido) {
        // Generar número de pedido si no existe
        if (pedido.getNumeroPedido() == null || pedido.getNumeroPedido().isEmpty()) {
            pedido.setNumeroPedido(generarNumeroPedido());
        }
        
        // Asegurar que cada ItemPedido tiene subtotal calculado antes de calcular totales.
        // Esto cubre casos donde los ItemPedido son objetos nuevos en memoria y
        // su @PrePersist no se ha ejecutado aún (por ejemplo cuando construimos el
        // Pedido manualmente en un controlador). Si falta precioUnitario o cantidad,
        // se deja el subtotal como cero para evitar NPE.
        if (pedido.getItems() != null) {
            for (var item : pedido.getItems()) {
                if (item.getSubtotal() == null) {
                    try {
                        if (item.getPrecioUnitario() != null && item.getCantidad() != null) {
                            item.setSubtotal(item.getPrecioUnitario().multiply(new java.math.BigDecimal(item.getCantidad())));
                        } else {
                            item.setSubtotal(java.math.BigDecimal.ZERO);
                        }
                    } catch (Exception ex) {
                        item.setSubtotal(java.math.BigDecimal.ZERO);
                    }
                }
            }
        }

        // Calcular totales
        pedido.calcularTotales();

        return pedidoRepository.save(pedido);
    }

    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }

    public List<Pedido> buscarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> buscarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public List<Pedido> buscarPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pedidoRepository.findByFechaPedidoBetween(fechaInicio, fechaFin);
    }

    public List<Pedido> buscar(String busqueda) {
        return pedidoRepository.buscarPedidos(busqueda);
    }

    public Pedido actualizarEstado(Long pedidoId, EstadoPedido nuevoEstado) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            pedido.setEstado(nuevoEstado);
            
            // Si se marca como entregado, registrar fecha de entrega
            if (nuevoEstado == EstadoPedido.ENTREGADO && pedido.getFechaEntregaReal() == null) {
                pedido.setFechaEntregaReal(LocalDateTime.now());
            }
            
            return pedidoRepository.save(pedido);
        }
        throw new RuntimeException("Pedido no encontrado con ID: " + pedidoId);
    }

    public Long contarPorEstado(EstadoPedido estado) {
        return pedidoRepository.countByEstado(estado);
    }

    public Double calcularVentasTotales(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pedidoRepository.calcularVentasTotales(fechaInicio, fechaFin);
    }

    private String generarNumeroPedido() {
        // Formato: PED-YYYYMMDD-XXXX
        String fecha = LocalDateTime.now().toString().substring(0, 10).replace("-", "");
        long count = pedidoRepository.count() + 1;
        return String.format("PED-%s-%04d", fecha, count);
    }

    public boolean existeNumeroPedido(String numeroPedido) {
        return pedidoRepository.findByNumeroPedido(numeroPedido).isPresent();
    }
}
