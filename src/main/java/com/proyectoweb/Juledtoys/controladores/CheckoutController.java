package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.servicios.CarritoServiceJPA;
import com.proyectoweb.Juledtoys.servicios.UsuarioService;
import com.proyectoweb.Juledtoys.servicios.ClienteService;
import com.proyectoweb.Juledtoys.entidades.Cliente;
import com.proyectoweb.Juledtoys.entidades.Direccion;
import com.proyectoweb.Juledtoys.modelos_old.Carrito;
import com.proyectoweb.Juledtoys.entidades.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.proyectoweb.Juledtoys.entidades.Pedido;
import com.proyectoweb.Juledtoys.entidades.ItemPedido;
import com.proyectoweb.Juledtoys.entidades.CarritoItem;
import com.proyectoweb.Juledtoys.entidades.Producto;
import com.proyectoweb.Juledtoys.servicios.PedidoService;
import com.proyectoweb.Juledtoys.repositorios.ProductoRepository;
import java.util.List;
import java.math.BigDecimal;

@Controller
public class CheckoutController {

    @Autowired
    private CarritoServiceJPA carritoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/checkout")
    public String checkout(Model model) {
        // Reutilizar la conversión usada en CarritoController
        var itemsEntidad = carritoService.obtenerItems();
        Carrito carritoVista = new Carrito();
        for (var ci : itemsEntidad) {
            if (ci.getProducto() == null) continue;
            var it = new com.proyectoweb.Juledtoys.modelos_old.ItemCarrito();
            it.setProductoId(ci.getProducto().getId());
            it.setNombre(ci.getProducto().getNombre());
            it.setPrecio(ci.getPrecioUnitario() != null ? ci.getPrecioUnitario().doubleValue() : ci.getProducto().getPrecio() != null ? ci.getProducto().getPrecio().doubleValue() : 0.0);
            it.setCantidad(ci.getCantidad());
            it.setImagenUrl(ci.getProducto().getImagenUrl());
            carritoVista.getItems().add(it);
        }

        model.addAttribute("carrito", carritoVista);
        model.addAttribute("total", carritoService.calcularTotal());
        model.addAttribute("cantidadTotal", carritoService.obtenerCantidadTotal());

        // Usuario o Cliente actual (si existe) y su dirección
        Usuario usuario = obtenerUsuarioActual();
        if (usuario != null) {
            model.addAttribute("usuarioEmail", usuario.getEmail());
            model.addAttribute("usuarioDireccion", usuario.getDireccion());
        } else {
            // Intentar cliente (clientes usan entidad Cliente)
            com.proyectoweb.Juledtoys.entidades.Cliente cliente = clienteService.buscarPorUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null);
            if (cliente != null) {
                model.addAttribute("usuarioEmail", cliente.getEmail());
                model.addAttribute("usuarioDireccion", cliente.getDireccion());
            } else {
                model.addAttribute("usuarioEmail", null);
                model.addAttribute("usuarioDireccion", null);
            }
        }

        return "checkout";
    }

    @PostMapping("/checkout/direccion")
    public String guardarDireccion(@RequestParam("direccion") String direccion,
                                   @RequestParam(value = "numero", required = false) String numero,
                                   @RequestParam(value = "celular", required = false) String celular,
                                   RedirectAttributes redirectAttributes) {
        // Primero intentamos guardar la dirección en la entidad Cliente (si existe)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            redirectAttributes.addFlashAttribute("mensaje", "Debes iniciar sesión para guardar una dirección");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/checkout";
        }

        StringBuilder full = new StringBuilder();
        if (direccion != null) full.append(direccion.trim());
        if (numero != null && !numero.isBlank()) full.append(" ").append(numero.trim());
        if (celular != null && !celular.isBlank()) full.append(" - ").append(celular.trim());

        // Intentar cliente
        Cliente cliente = clienteService.buscarPorUsername(auth.getName()).orElse(null);
        if (cliente != null) {
            Direccion d = cliente.getDireccionEntity();
            if (d == null) {
                d = new Direccion();
                d.setCliente(cliente);
                cliente.getDirecciones().add(d);
            }
            d.setDireccionCompleta(full.toString());
            d.setNumero(numero);
            d.setTelefono(celular);
            clienteService.guardar(cliente);

            redirectAttributes.addFlashAttribute("mensaje", "Dirección guardada");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/checkout";
        }

        // Si no hay Cliente, caemos al comportamiento existente sobre Usuario (retrocompatibilidad)
        Usuario usuario = obtenerUsuarioActual();
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Debes iniciar sesión para guardar una dirección");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/checkout";
        }

        usuario.setDireccion(full.toString());
        usuarioService.guardar(usuario);

        redirectAttributes.addFlashAttribute("mensaje", "Dirección guardada");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        return "redirect:/checkout";
    }

    /**
     * Procesar pago y crear pedido en el sistema.
     * Requisitos mínimos: usuario debe existir como Cliente para asociar el pedido.
     */
    @PostMapping("/checkout/pagar")
    public String procesarPago(@RequestParam(value = "medioPago", required = false) String medioPago,
                               RedirectAttributes redirectAttributes) {
        // Obtener usuario autenticado y cliente
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            redirectAttributes.addFlashAttribute("mensaje", "Debes iniciar sesión para completar la compra");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/checkout";
        }

        Cliente cliente = clienteService.buscarPorUsername(auth.getName()).orElse(null);
        if (cliente == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Sólo clientes registrados pueden completar la compra");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/checkout";
        }

        // Obtener items del carrito
        List<CarritoItem> items = carritoService.obtenerItems();
        if (items == null || items.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "El carrito está vacío");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/checkout";
        }

        // Construir Pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setMetodoPago(medioPago != null ? medioPago : "no especificado");
        // dirección (priorizar dirección del cliente)
        String direccionEnvio = cliente.getDireccionEntity() != null && cliente.getDireccionEntity().getDireccionCompleta() != null
                ? cliente.getDireccionEntity().getDireccionCompleta()
                : cliente.getDireccion();
        pedido.setDireccionEnvio(direccionEnvio != null ? direccionEnvio : "");

        // Convertir items del carrito a items de pedido y reducir stock
        for (CarritoItem ci : items) {
            Producto producto = ci.getProducto();
            if (producto == null) continue;
            Integer cantidad = ci.getCantidad() != null ? ci.getCantidad() : 1;

            // Crear ItemPedido
            ItemPedido ip = new ItemPedido(producto, cantidad, producto.getPrecio());
            pedido.addItem(ip);

            // Reducir stock y persistir producto
            try {
                producto.reducirStock(cantidad);
            } catch (IllegalArgumentException ex) {
                // Stock insuficiente: informar y abortar
                redirectAttributes.addFlashAttribute("mensaje", "Stock insuficiente para el producto: " + producto.getNombre());
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/carrito";
            }
            productoRepository.save(producto);
        }

        // Calcular totales y persistir pedido
        pedido.calcularTotales();
        Pedido pedidoGuardado = pedidoService.guardar(pedido);

        // Vaciar carrito
        carritoService.vaciarCarrito();

        redirectAttributes.addFlashAttribute("mensaje", "Compra procesada. Pedido #: " + pedidoGuardado.getNumeroPedido());
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        return "redirect:/"; // redirigir al inicio; el pedido ya está en backoffice
    }

    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return usuarioService.buscarPorUsername(auth.getName()).orElse(null);
        }
        return null;
    }
}
