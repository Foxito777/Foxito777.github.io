package com.proyectoweb.Juledtoys.repositorios;

import com.proyectoweb.Juledtoys.entidades.Compra;
import com.proyectoweb.Juledtoys.entidades.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    // Buscar compras por proveedor
    List<Compra> findByProveedorOrderByFechaCompraDesc(Proveedor proveedor);

    // Buscar por número de orden
    Optional<Compra> findByNumeroOrden(String numeroOrden);

    // Buscar por estado
    List<Compra> findByEstadoOrderByFechaCompraDesc(String estado);

    // Buscar compras pendientes de un proveedor
    List<Compra> findByProveedorAndEstadoOrderByFechaCompraDesc(Proveedor proveedor, String estado);
}
