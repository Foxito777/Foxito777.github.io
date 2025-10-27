package com.proyectoweb.Juledtoys.servicios;

import com.proyectoweb.Juledtoys.entidades.CarritoItem;
import com.proyectoweb.Juledtoys.entidades.Producto;
import com.proyectoweb.Juledtoys.entidades.Usuario;
import com.proyectoweb.Juledtoys.repositorios.CarritoItemRepository;
import com.proyectoweb.Juledtoys.repositorios.ProductoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarritoServiceJPA {

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private HttpSession session;

    /**
     * Agregar producto al carrito
     */
    public void agregarProducto(Long productoId, Integer cantidad) {
        Preconditions.checkNotNull(productoId, "productoId no puede ser null");
        Preconditions.checkArgument(cantidad != null && cantidad > 0, "cantidad debe ser mayor que 0");

        Optional<Producto> productoOpt = productoRepository.findByIdAndDisponibleTrue(productoId);
        
        if (productoOpt.isEmpty()) {
            throw new IllegalArgumentException("Producto no válido o no disponible");
        }

        Producto producto = productoOpt.get();
        // NOTA: por requerimiento UX permitimos agregar productos incluso si el stock actual es 0.
        // La validación de stock se realizará en la etapa de "Continuar" desde la vista del carrito.

        Usuario usuario = obtenerUsuarioActual();
        Optional<CarritoItem> itemExistente;

        if (usuario != null) {
            // Usuario registrado
            itemExistente = carritoItemRepository.findByUsuarioAndProducto(usuario, producto);
        } else {
            // Usuario anónimo (por sesión)
            String sessionId = session.getId();
            itemExistente = carritoItemRepository.findBySessionIdAndProducto(sessionId, producto);
        }

        if (itemExistente.isPresent()) {
            // Actualizar cantidad del item existente (sin validar stock aquí)
            CarritoItem item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;
            item.setCantidad(nuevaCantidad);
            carritoItemRepository.save(item);
        } else {
            // Crear nuevo item
            CarritoItem nuevoItem;
            if (usuario != null) {
                nuevoItem = new CarritoItem(usuario, producto, cantidad);
            } else {
                nuevoItem = new CarritoItem(session.getId(), producto, cantidad);
            }
            carritoItemRepository.save(nuevoItem);
        }
    }

    /**
     * Actualizar cantidad de un producto en el carrito
     */
    public void actualizarCantidad(Long productoId, Integer nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            eliminarProducto(productoId);
            return;
        }

        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        Producto producto = productoOpt.get();
        // No validamos stock aquí: la comprobación se hace en la fase de "Continuar".

        Usuario usuario = obtenerUsuarioActual();
        Optional<CarritoItem> itemOpt;

        if (usuario != null) {
            itemOpt = carritoItemRepository.findByUsuarioAndProducto(usuario, producto);
        } else {
            itemOpt = carritoItemRepository.findBySessionIdAndProducto(session.getId(), producto);
        }

        if (itemOpt.isPresent()) {
            CarritoItem item = itemOpt.get();
            item.setCantidad(nuevaCantidad);
            carritoItemRepository.save(item);
        }
    }

    /**
     * Eliminar producto del carrito
     */
    public void eliminarProducto(Long productoId) {
        Usuario usuario = obtenerUsuarioActual();
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        
        if (productoOpt.isEmpty()) {
            return;
        }

        Producto producto = productoOpt.get();
        Optional<CarritoItem> itemOpt;

        if (usuario != null) {
            itemOpt = carritoItemRepository.findByUsuarioAndProducto(usuario, producto);
        } else {
            itemOpt = carritoItemRepository.findBySessionIdAndProducto(session.getId(), producto);
        }

        itemOpt.ifPresent(carritoItemRepository::delete);
    }

    /**
     * Obtener items del carrito
     */
    public List<CarritoItem> obtenerItems() {
        Usuario usuario = obtenerUsuarioActual();
        
        if (usuario != null) {
            return ImmutableList.copyOf(carritoItemRepository.findByUsuario(usuario));
        } else {
            return ImmutableList.copyOf(carritoItemRepository.findBySessionId(session.getId()));
        }
    }

    /**
     * Obtener cantidad total de productos en el carrito
     */
    public Integer obtenerCantidadTotal() {
        Usuario usuario = obtenerUsuarioActual();
        
        if (usuario != null) {
            return carritoItemRepository.obtenerCantidadTotalUsuario(usuario);
        } else {
            return carritoItemRepository.obtenerCantidadTotalSesion(session.getId());
        }
    }

    /**
     * Calcular total del carrito
     */
    public BigDecimal calcularTotal() {
        Usuario usuario = obtenerUsuarioActual();
        
        if (usuario != null) {
            return carritoItemRepository.calcularTotalCarritoUsuario(usuario);
        } else {
            return carritoItemRepository.calcularTotalCarritoSesion(session.getId());
        }
    }

    /**
     * Vaciar el carrito
     */
    public void vaciarCarrito() {
        Usuario usuario = obtenerUsuarioActual();
        
        if (usuario != null) {
            carritoItemRepository.deleteByUsuario(usuario);
        } else {
            carritoItemRepository.deleteBySessionId(session.getId());
        }
    }

    /**
     * Migrar carrito de sesión a usuario (cuando se registra/loguea)
     */
    public void migrarCarritoSesionAUsuario(Usuario usuario) {
        String sessionId = session.getId();
        List<CarritoItem> itemsSesion = carritoItemRepository.findBySessionId(sessionId);
        
        for (CarritoItem item : itemsSesion) {
            // Verificar si el usuario ya tiene este producto en su carrito
            Optional<CarritoItem> itemUsuarioOpt = carritoItemRepository.findByUsuarioAndProducto(usuario, item.getProducto());
            
            if (itemUsuarioOpt.isPresent()) {
                // Sumar cantidades
                CarritoItem itemUsuario = itemUsuarioOpt.get();
                itemUsuario.setCantidad(itemUsuario.getCantidad() + item.getCantidad());
                carritoItemRepository.save(itemUsuario);
            } else {
                // Transferir item a usuario
                item.setUsuario(usuario);
                item.setSessionId(null);
                carritoItemRepository.save(item);
            }
        }
        
        // Eliminar items de sesión que no se transfirieron
        carritoItemRepository.deleteBySessionId(sessionId);
    }

    /**
     * Limpiar todo el carrito del usuario actual
     */
    public void limpiarCarrito() {
        Usuario usuario = obtenerUsuarioActual();
        HttpSession session = obtenerSesionActual();
        
        if (usuario != null) {
            carritoItemRepository.deleteByUsuario(usuario);
        } else if (session != null) {
            carritoItemRepository.deleteBySessionId(session.getId());
        }
    }

    /**
     * Limpiar carritos abandonados (tarea de mantenimiento)
     */
    public void limpiarCarritosAbandonados() {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(7); // 7 días de antigüedad
        List<CarritoItem> carritosAbandonados = carritoItemRepository.findCarritosAbandonados(fechaLimite);
        carritoItemRepository.deleteAll(carritosAbandonados);
    }

    /**
     * Obtener usuario actual desde el contexto de seguridad
     */
    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return usuarioService.buscarPorUsername(auth.getName()).orElse(null);
        }
        return null;
    }

    /**
     * Obtener sesión actual desde el contexto HTTP
     */
    private HttpSession obtenerSesionActual() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                return ((ServletRequestAttributes) requestAttributes).getRequest().getSession(false);
            }
        } catch (Exception e) {
            // Si no hay contexto HTTP disponible
        }
        return null;
    }
}