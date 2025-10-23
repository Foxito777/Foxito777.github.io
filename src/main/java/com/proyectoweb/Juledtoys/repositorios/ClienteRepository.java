package com.proyectoweb.Juledtoys.repositorios;

import com.proyectoweb.Juledtoys.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsername(String username);
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<Cliente> findByActivoTrue();
}
