package com.proyectoweb.Juledtoys.config;

import com.proyectoweb.Juledtoys.entidades.Cliente;
import com.proyectoweb.Juledtoys.entidades.Pedido;
import com.proyectoweb.Juledtoys.servicios.ClienteService;
import com.proyectoweb.Juledtoys.servicios.PedidoService;
import com.proyectoweb.Juledtoys.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService;

    private Optional<String> getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return Optional.empty();
        if (!auth.isAuthenticated()) return Optional.empty();
        if (auth instanceof AnonymousAuthenticationToken) return Optional.empty();
        return Optional.ofNullable(auth.getName());
    }

    @ModelAttribute("usuarioNombre")
    public String usuarioNombre() {
        try {
            Optional<String> nameOpt = getAuthenticatedUsername();
            if (nameOpt.isEmpty()) return null;
            String username = nameOpt.get();
            // Intentar obtener cliente
            var clienteOpt = clienteService.buscarPorUsername(username);
            if (clienteOpt.isPresent()) {
                Cliente c = clienteOpt.get();
                return (c.getNombreCompleto() != null && !c.getNombreCompleto().isBlank()) ? c.getNombreCompleto() : c.getUsername();
            }
            // Intentar obtener usuario staff
            var usuarioOpt = usuarioService.buscarPorUsername(username);
            if (usuarioOpt.isPresent()) {
                return usuarioOpt.get().getUsername();
            }
        } catch (Exception e) {
            // no bloquear la vista
        }
        return null;
    }

    @ModelAttribute("usuarioEmail")
    public String usuarioEmail() {
        try {
            Optional<String> nameOpt = getAuthenticatedUsername();
            if (nameOpt.isEmpty()) return null;
            String username = nameOpt.get();
            var clienteOpt = clienteService.buscarPorUsername(username);
            if (clienteOpt.isPresent()) return clienteOpt.get().getEmail();
            var usuarioOpt = usuarioService.buscarPorUsername(username);
            if (usuarioOpt.isPresent()) return usuarioOpt.get().getEmail();
        } catch (Exception e) {
        }
        return null;
    }

    @ModelAttribute("usuarioId")
    public Long usuarioId() {
        try {
            Optional<String> nameOpt = getAuthenticatedUsername();
            if (nameOpt.isEmpty()) return null;
            String username = nameOpt.get();
            var clienteOpt = clienteService.buscarPorUsername(username);
            if (clienteOpt.isPresent()) return clienteOpt.get().getId();
        } catch (Exception e) {
        }
        return null;
    }

    @ModelAttribute("usuarioPedidoCount")
    public Integer usuarioPedidoCount() {
        try {
            Long id = usuarioId();
            if (id == null) return 0;
            List<Pedido> pedidos = pedidoService.buscarPorCliente(id);
            return (pedidos != null) ? pedidos.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @ModelAttribute("usuarioUltimoPedidoEstado")
    public String usuarioUltimoPedidoEstado() {
        try {
            Long id = usuarioId();
            if (id == null) return null;
            List<Pedido> pedidos = pedidoService.buscarPorCliente(id);
            if (pedidos == null || pedidos.isEmpty()) return null;
            Optional<Pedido> ultimo = pedidos.stream().max(Comparator.comparing(Pedido::getFechaPedido));
            return ultimo.map(p -> (p.getEstado() != null) ? p.getEstado().name() : null).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
