package com.proyectoweb.Juledtoys.repositorios;

import com.proyectoweb.Juledtoys.entidades.Pedido;
import com.proyectoweb.Juledtoys.entidades.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByClienteId(Long clienteId);

    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPedido DESC")
    List<Pedido> findByFechaPedidoBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                          @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT p FROM Pedido p WHERE p.estado = :estado AND p.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    List<Pedido> findByEstadoAndFechaPedidoBetween(@Param("estado") EstadoPedido estado,
                                                    @Param("fechaInicio") LocalDateTime fechaInicio,
                                                    @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT p FROM Pedido p WHERE LOWER(p.numeroPedido) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(p.cliente.nombreCompleto) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(p.cliente.email) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Pedido> buscarPedidos(@Param("busqueda") String busqueda);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long countByEstado(@Param("estado") EstadoPedido estado);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.estado = 'ENTREGADO' " +
           "AND p.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    Double calcularVentasTotales(@Param("fechaInicio") LocalDateTime fechaInicio,
                                  @Param("fechaFin") LocalDateTime fechaFin);
}
