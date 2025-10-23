package com.proyectoweb.Juledtoys.repositorios;

import com.proyectoweb.Juledtoys.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar por username
    Optional<Usuario> findByUsername(String username);

    // Buscar por email
    Optional<Usuario> findByEmail(String email);

    // Buscar por username o email (para login)
    @Query("SELECT u FROM Usuario u WHERE u.username = :identificador OR u.email = :identificador")
    Optional<Usuario> findByUsernameOrEmail(@Param("identificador") String identificador);

    // Verificar si existe username
    boolean existsByUsername(String username);

    // Verificar si existe email
    boolean existsByEmail(String email);

    // Buscar usuarios activos
    List<Usuario> findByActivoTrue();

    // Buscar usuarios por rol
    List<Usuario> findByRol(Usuario.Rol rol);

    // Buscar usuarios registrados en un período
    List<Usuario> findByFechaRegistroBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Buscar usuarios con último acceso reciente
    List<Usuario> findByUltimoAccesoAfter(LocalDateTime fecha);

    // Contar usuarios activos
    long countByActivoTrue();

    // Contar usuarios por rol
    long countByRol(Usuario.Rol rol);

    // Buscar usuarios por rol y estado activo
    List<Usuario> findByRolAndActivoTrue(Usuario.Rol rol);
}