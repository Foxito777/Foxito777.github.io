package com.proyectoweb.Juledtoys.repositorios;

import com.proyectoweb.Juledtoys.entidades.EntregaProveedor;
import com.proyectoweb.Juledtoys.entidades.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntregaProveedorRepository extends JpaRepository<EntregaProveedor, Long> {

    // Buscar entregas por proveedor
    List<EntregaProveedor> findByProveedorOrderByFechaEntregaDesc(Proveedor proveedor);

    // Buscar por número de guía
    Optional<EntregaProveedor> findByNumeroGuia(String numeroGuia);

    // Buscar por estado
    List<EntregaProveedor> findByEstadoOrderByFechaEntregaDesc(String estado);

    // Buscar entregas en tránsito de un proveedor
    List<EntregaProveedor> findByProveedorAndEstadoOrderByFechaEntregaDesc(Proveedor proveedor, String estado);
}
