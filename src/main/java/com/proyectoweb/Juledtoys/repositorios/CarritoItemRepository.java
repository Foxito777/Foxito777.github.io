package com.proyectoweb.Juledtoys.repositorios;

import com.proyectoweb.Juledtoys.entidades.CarritoItem;
import com.proyectoweb.Juledtoys.entidades.Usuario;
import com.proyectoweb.Juledtoys.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    // Buscar items del carrito por usuario
    List<CarritoItem> findByUsuario(Usuario usuario);

    // Buscar items del carrito por session ID (usuarios no registrados)
    List<CarritoItem> findBySessionId(String sessionId);

    // Buscar item específico por usuario y producto
    Optional<CarritoItem> findByUsuarioAndProducto(Usuario usuario, Producto producto);

    // Buscar item específico por session ID y producto
    Optional<CarritoItem> findBySessionIdAndProducto(String sessionId, Producto producto);

    // Eliminar todos los items de un usuario
    void deleteByUsuario(Usuario usuario);

    // Eliminar todos los items de una sesión
    void deleteBySessionId(String sessionId);

    // Contar items en el carrito de un usuario
    long countByUsuario(Usuario usuario);

    // Contar items en el carrito de una sesión
    long countBySessionId(String sessionId);

    // Calcular total del carrito de un usuario
    @Query("SELECT COALESCE(SUM(ci.precioUnitario * ci.cantidad), 0) FROM CarritoItem ci WHERE ci.usuario = :usuario")
    BigDecimal calcularTotalCarritoUsuario(@Param("usuario") Usuario usuario);

    // Calcular total del carrito de una sesión
    @Query("SELECT COALESCE(SUM(ci.precioUnitario * ci.cantidad), 0) FROM CarritoItem ci WHERE ci.sessionId = :sessionId")
    BigDecimal calcularTotalCarritoSesion(@Param("sessionId") String sessionId);

    // Obtener cantidad total de productos en carrito por usuario
    @Query("SELECT COALESCE(SUM(ci.cantidad), 0) FROM CarritoItem ci WHERE ci.usuario = :usuario")
    Integer obtenerCantidadTotalUsuario(@Param("usuario") Usuario usuario);

    // Obtener cantidad total de productos en carrito por sesión
    @Query("SELECT COALESCE(SUM(ci.cantidad), 0) FROM CarritoItem ci WHERE ci.sessionId = :sessionId")
    Integer obtenerCantidadTotalSesion(@Param("sessionId") String sessionId);

    // Buscar carritos abandonados (items antiguos de sesiones)
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.sessionId IS NOT NULL AND ci.fechaAgregado < :fechaLimite")
    List<CarritoItem> findCarritosAbandonados(@Param("fechaLimite") java.time.LocalDateTime fechaLimite);
}