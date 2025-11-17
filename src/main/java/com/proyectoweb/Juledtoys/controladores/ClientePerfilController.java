package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.entidades.Cliente;
import com.proyectoweb.Juledtoys.entidades.Pedido;
import com.proyectoweb.Juledtoys.servicios.ClienteService;
import com.proyectoweb.Juledtoys.servicios.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ClientePerfilController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PedidoService pedidoService;

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        if (!auth.isAuthenticated()) return null;
        if (auth instanceof AnonymousAuthenticationToken) return null;
        return auth.getName();
    }

    @GetMapping("/mi-cuenta")
    public String miCuenta(Model model) {
        String username = getAuthenticatedUsername();
        if (username == null) {
            return "redirect:/login";
        }
        var clienteOpt = clienteService.buscarPorUsername(username);
        if (clienteOpt.isEmpty()) {
            return "redirect:/login";
        }
        Cliente cliente = clienteOpt.get();
        model.addAttribute("cliente", cliente);
        List<Pedido> pedidos = pedidoService.buscarPorCliente(cliente.getId());
        model.addAttribute("pedidos", pedidos);
        return "mi-cuenta";
    }

    @GetMapping("/mis-compras")
    public String misCompras(Model model) {
        String username = getAuthenticatedUsername();
        System.out.println("✅ [mis-compras] Usuario autenticado: " + username);
        
        if (username == null) {
            System.out.println("❌ [mis-compras] Usuario no autenticado, redirigiendo a login");
            return "redirect:/login";
        }
        
        var clienteOpt = clienteService.buscarPorUsername(username);
        if (clienteOpt.isEmpty()) {
            System.out.println("❌ [mis-compras] Cliente no encontrado para username: " + username);
            return "redirect:/login";
        }
        
        Cliente cliente = clienteOpt.get();
        System.out.println("✅ [mis-compras] Cliente encontrado: ID=" + cliente.getId() + ", Username=" + cliente.getUsername());
        
        List<Pedido> pedidos = pedidoService.buscarPorCliente(cliente.getId());
        System.out.println("✅ [mis-compras] Pedidos encontrados: " + (pedidos != null ? pedidos.size() : "NULL"));
        
        if (pedidos != null && !pedidos.isEmpty()) {
            for (Pedido p : pedidos) {
                System.out.println("   📦 Pedido #" + p.getNumeroPedido() + " - Estado: " + p.getEstado() + " - Total: S/" + p.getTotal());
            }
        } else {
            System.out.println("   ⚠️ No hay pedidos para este cliente");
        }
        
        model.addAttribute("pedidos", pedidos);
        return "mis-compras";
    }

    @GetMapping("/editar-perfil")
    public String editarPerfil(Model model) {
        String username = getAuthenticatedUsername();
        if (username == null) {
            return "redirect:/login";
        }
        var clienteOpt = clienteService.buscarPorUsername(username);
        if (clienteOpt.isEmpty()) {
            return "redirect:/login";
        }
        Cliente cliente = clienteOpt.get();
        model.addAttribute("cliente", cliente);
        return "editar-perfil";
    }

    @GetMapping("/calificar-pedido/{pedidoId}")
    public String calificarPedido(@org.springframework.web.bind.annotation.PathVariable Long pedidoId, Model model) {
        String username = getAuthenticatedUsername();
        if (username == null) {
            return "redirect:/login";
        }
        var clienteOpt = clienteService.buscarPorUsername(username);
        if (clienteOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        // Verificar que el pedido existe y pertenece al cliente
        var pedidoOpt = pedidoService.buscarPorId(pedidoId);
        if (pedidoOpt.isEmpty()) {
            return "redirect:/mis-compras";
        }
        
        Pedido pedido = pedidoOpt.get();
        Cliente cliente = clienteOpt.get();
        
        // Verificar que el pedido pertenece al cliente actual
        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            return "redirect:/mis-compras";
        }
        
        model.addAttribute("pedido", pedido);
        return "calificar-pedido";
    }
}
