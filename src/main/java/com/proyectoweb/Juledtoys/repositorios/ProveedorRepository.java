package com.proyectoweb.Juledtoys.repositorios;

import com.proyectoweb.Juledtoys.entidades.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    // Buscar por RUC
    Optional<Proveedor> findByRuc(String ruc);

    // Buscar proveedores activos
    List<Proveedor> findByActivoTrue();

    // Buscar por razón social (contiene, ignorando mayúsculas)
    @Query("SELECT p FROM Proveedor p WHERE LOWER(p.razonSocial) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Proveedor> buscarPorRazonSocial(@Param("query") String query);

    // Búsqueda general por razón social, RUC o persona de contacto
    @Query("SELECT p FROM Proveedor p WHERE " +
           "LOWER(p.razonSocial) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.ruc) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.personaContacto) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Proveedor> buscarGeneral(@Param("query") String query);

    // Contar proveedores activos
    long countByActivoTrue();
}
