package com.proyectoweb.Juledtoys.servicios;

import com.proyectoweb.Juledtoys.entidades.Cliente;
import com.proyectoweb.Juledtoys.repositorios.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // La autenticación de clientes se maneja centralmente en UsuarioService

    public Cliente registrar(Cliente cliente) {
        if (clienteRepository.existsByUsername(cliente.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> buscarPorId(Long id) { return clienteRepository.findById(id); }
    public Optional<Cliente> buscarPorUsername(String username) { return clienteRepository.findByUsername(username); }
    public Optional<Cliente> buscarPorEmail(String email) { return clienteRepository.findByEmail(email); }
    public List<Cliente> listarTodos() { return clienteRepository.findAll(); }
    public List<Cliente> listarActivos() { return clienteRepository.findByActivoTrue(); }
    public Cliente guardar(Cliente c) { return clienteRepository.save(c); }

    /**
     * Elimina un cliente físicamente de la base de datos por su id.
     * Usar con precaución: esta operación es irreversible.
     */
    public void eliminarPorId(Long id) {
        clienteRepository.deleteById(id);
    }
}
