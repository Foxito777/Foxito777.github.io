package com.proyectoweb.Juledtoys.repositorios;

import com.proyectoweb.Juledtoys.entidades.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    List<ItemPedido> findByPedidoId(Long pedidoId);

    List<ItemPedido> findByProductoId(Long productoId);

    @Query("SELECT ip FROM ItemPedido ip WHERE ip.pedido.id = :pedidoId")
    List<ItemPedido> findItemsByPedidoId(@Param("pedidoId") Long pedidoId);

    @Query("SELECT ip.producto.nombre, SUM(ip.cantidad) as total " +
           "FROM ItemPedido ip " +
           "JOIN ip.pedido p " +
           "WHERE p.estado = 'ENTREGADO' AND p.fechaPedido BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY ip.producto.id, ip.producto.nombre " +
           "ORDER BY total DESC")
    List<Object[]> findProductosMasVendidos(@Param("fechaInicio") LocalDateTime fechaInicio,
                                            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COALESCE(SUM(ip.cantidad), 0) FROM ItemPedido ip " +
           "WHERE ip.producto.id = :productoId " +
           "AND ip.pedido.estado = 'ENTREGADO' " +
           "AND ip.pedido.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    Integer calcularCantidadVendida(@Param("productoId") Long productoId,
                                    @Param("fechaInicio") LocalDateTime fechaInicio,
                                    @Param("fechaFin") LocalDateTime fechaFin);
}
