package com.proyectoweb.Juledtoys.repositorios;

import com.proyectoweb.Juledtoys.entidades.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para Promociones.
 */
@Repository
public interface PromocionRepositoryJPA extends JpaRepository<Promocion, Long> {
    
    @Query("SELECT p FROM Promocion p WHERE p.destacado = true ORDER BY p.orden ASC")
    List<Promocion> findDestacadasOrdenadas();
    
    @Query("SELECT p FROM Promocion p WHERE p.estado = :estado ORDER BY p.orden ASC")
    List<Promocion> findByEstadoOrderByOrdenAsc(String estado);
    
    @Query("SELECT p FROM Promocion p WHERE p.inicio <= :fecha AND (p.fin IS NULL OR p.fin >= :fecha) AND p.estado = 'ACTIVA'")
    List<Promocion> findActivasEnFecha(LocalDate fecha);
    
    List<Promocion> findByDestacadoTrueOrderByOrdenAsc();
    
    List<Promocion> findAllByOrderByOrdenAsc();
    
    // Métodos adicionales requeridos por el servicio
    @Query("SELECT p FROM Promocion p WHERE p.estado = 'ACTIVA' ORDER BY p.orden ASC")
    List<Promocion> findActivas();
    
    @Query("SELECT p FROM Promocion p WHERE p.destacado = true ORDER BY p.orden ASC")
    List<Promocion> findDestacadas();
    
    @Query("SELECT COUNT(p) FROM Promocion p WHERE p.estado = 'ACTIVA'")
    long countActivas();
    
    @Query("SELECT COUNT(p) FROM Promocion p WHERE p.estado = 'PROGRAMADA'")
    long countProgramadas();
    
    @Query("SELECT COUNT(p) FROM Promocion p WHERE p.estado = 'FINALIZADA'")
    long countFinalizadas();
    
    // Método para reordenar promociones
    @Modifying
    @Transactional
    @Query("UPDATE Promocion p SET p.orden = :#{#ids.indexOf(p.id) + 1} WHERE p.id IN :ids")
    void reorder(@Param("ids") List<Long> idsOrdenados);
}