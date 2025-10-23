package com.proyectoweb.Juledtoys.repositorios;

import com.proyectoweb.Juledtoys.entidades.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepositoryJPAInterface extends JpaRepository<Producto, Long> {

    // Buscar productos disponibles
    List<Producto> findByDisponibleTrue();

    // Buscar por categoría
    List<Producto> findByCategoriaAndDisponibleTrue(String categoria);

    // Buscar por nombre (contiene texto)
    List<Producto> findByNombreContainingIgnoreCaseAndDisponibleTrue(String nombre);

    // Buscar por rango de precios
    List<Producto> findByPrecioBetweenAndDisponibleTrue(BigDecimal precioMin, BigDecimal precioMax);

    // Buscar productos con stock
    List<Producto> findByStockGreaterThanAndDisponibleTrue(Integer stock);

    // Buscar por categoría con paginación
    Page<Producto> findByCategoriaAndDisponibleTrue(String categoria, Pageable pageable);

    // Buscar todos los disponibles con paginación
    Page<Producto> findByDisponibleTrue(Pageable pageable);

    // Consulta personalizada: buscar productos por múltiples criterios
    @Query("SELECT p FROM Producto p WHERE " +
           "(:categoria IS NULL OR p.categoria = :categoria) AND " +
           "(:precioMin IS NULL OR p.precio >= :precioMin) AND " +
           "(:precioMax IS NULL OR p.precio <= :precioMax) AND " +
           "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "p.disponible = true")
    Page<Producto> buscarConFiltros(@Param("categoria") String categoria,
                                   @Param("precioMin") BigDecimal precioMin,
                                   @Param("precioMax") BigDecimal precioMax,
                                   @Param("nombre") String nombre,
                                   Pageable pageable);

    // Obtener categorías únicas
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.categoria IS NOT NULL AND p.disponible = true")
    List<String> findDistinctCategorias();

    // Top productos (los más nuevos)
    List<Producto> findTop10ByDisponibleTrueOrderByFechaCreacionDesc();

    // Productos con stock bajo (menos de X unidades)
    List<Producto> findByStockLessThanAndDisponibleTrue(Integer stockMinimo);

    // Verificar si existe producto por nombre (para validaciones)
    boolean existsByNombreIgnoreCase(String nombre);

    // Buscar producto por ID y que esté disponible
    Optional<Producto> findByIdAndDisponibleTrue(Long id);
}