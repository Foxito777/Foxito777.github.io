package com.proyectoweb.Juledtoys.servicios;
import com.proyectoweb.Juledtoys.entidades.Usuario;
import com.proyectoweb.Juledtoys.entidades.Cliente;
import com.proyectoweb.Juledtoys.repositorios.UsuarioRepository;
import com.proyectoweb.Juledtoys.repositorios.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Implementación de UserDetailsService para Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar primero en usuarios (staff)
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameOrEmail(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (!usuario.isActivo()) {
                throw new UsernameNotFoundException("Usuario inactivo: " + username);
            }
            usuario.actualizarUltimoAcceso();
            usuarioRepository.save(usuario);
            return usuario;
        }

        // Si no es usuario, buscar como cliente
    Cliente cliente = clienteRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        if (!cliente.isActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }
        cliente.actualizarUltimoAcceso();
        clienteRepository.save(cliente);
        return cliente;
    }

    /**
     * Registrar nuevo usuario
     */
    public Usuario registrarUsuario(Usuario usuario) {
        // Validar que no exista el username o email
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Establecer rol por defecto para usuarios del staff
        if (usuario.getRol() == null) {
            usuario.setRol(Usuario.Rol.VENDEDOR);
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Buscar usuario por ID
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Buscar usuario por username
     */
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Buscar usuario por email
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Actualizar información del usuario
     */
    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Cambiar contraseña
     */
    public void cambiarPassword(Long usuarioId, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        // Actualizar contraseña
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

    /**
     * Activar/desactivar usuario
     */
    public void cambiarEstadoUsuario(Long usuarioId, boolean activo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
    }

    /**
     * Obtener todos los usuarios activos
     */
    public List<Usuario> obtenerUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    /**
     * Obtener usuarios por rol
     */
    public List<Usuario> obtenerUsuariosPorRol(Usuario.Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    /**
     * Obtener usuarios registrados en un período
     */
    public List<Usuario> obtenerUsuariosRegistradosEnPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return usuarioRepository.findByFechaRegistroBetween(fechaInicio, fechaFin);
    }

    /**
     * Contar usuarios activos
     */
    public long contarUsuariosActivos() {
        return usuarioRepository.countByActivoTrue();
    }

    /**
     * Contar usuarios por rol
     */
    public long contarUsuariosPorRol(Usuario.Rol rol) {
        return usuarioRepository.countByRol(rol);
    }

    /**
     * Verificar si un usuario existe por username
     */
    public boolean existePorUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    /**
     * Verificar si un usuario existe por email
     */
    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /**
     * Listar todos los usuarios
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtener usuario por ID (sin Optional, retorna null si no existe)
     */
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    /**
     * Guardar usuario
     */
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Verificar si existe username
     */
    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    /**
     * Verificar si existe email
     */
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /**
     * Eliminar usuario por ID (borrado físico)
     */
    public void eliminarPorId(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Métodos de clientes fueron migrados a ClienteService
}
