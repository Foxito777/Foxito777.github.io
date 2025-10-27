package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.servicios.CarritoServiceJPA;
import com.proyectoweb.Juledtoys.modelos_old.Carrito;
import com.proyectoweb.Juledtoys.modelos_old.ItemCarrito;
import com.proyectoweb.Juledtoys.entidades.CarritoItem;
import com.proyectoweb.Juledtoys.entidades.Producto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoServiceJPA carritoService;

    @GetMapping
    public String verCarrito(Model model) {
        // Obtener items desde el servicio (entidades JPA)
        var itemsEntidad = carritoService.obtenerItems();

        // Convertir a modelo de vista usado por la plantilla (`modelos_old.Carrito` / `ItemCarrito`)
        Carrito carritoVista = new Carrito();
        for (var ci : itemsEntidad) {
            if (ci.getProducto() == null) continue;
            ItemCarrito it = new ItemCarrito();
            it.setProductoId(ci.getProducto().getId());
            it.setNombre(ci.getProducto().getNombre());
            // convertir BigDecimal a Double para la vista antigua
            it.setPrecio(ci.getPrecioUnitario() != null ? ci.getPrecioUnitario().doubleValue() : ci.getProducto().getPrecio() != null ? ci.getProducto().getPrecio().doubleValue() : 0.0);
            it.setCantidad(ci.getCantidad());
            it.setImagenUrl(ci.getProducto().getImagenUrl());
            // Construir ItemCarrito (modelo de vista) y añadir directamente
            ItemCarrito itView = new ItemCarrito();
            itView.setProductoId(ci.getProducto().getId());
            itView.setNombre(ci.getProducto().getNombre());
            itView.setPrecio(ci.getPrecioUnitario() != null ? ci.getPrecioUnitario().doubleValue() : ci.getProducto().getPrecio() != null ? ci.getProducto().getPrecio().doubleValue() : 0.0);
            itView.setCantidad(ci.getCantidad());
            itView.setImagenUrl(ci.getProducto().getImagenUrl());
            carritoVista.getItems().add(itView);
        }

        model.addAttribute("carrito", carritoVista);
        // total y cantidad provienen del servicio (BigDecimal/Integer)
        model.addAttribute("total", carritoService.calcularTotal());
        model.addAttribute("cantidadTotal", carritoService.obtenerCantidadTotal());
        return "carrito";
    }

    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam Long productoId, 
                                @RequestParam(defaultValue = "1") Integer cantidad,
                                RedirectAttributes redirectAttributes) {
        try {
            carritoService.agregarProducto(productoId, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado al carrito exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al agregar producto al carrito");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/productos";
    }

    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam Long productoId, 
                                   @RequestParam Integer cantidad,
                                   RedirectAttributes redirectAttributes) {
        try {
            carritoService.actualizarCantidad(productoId, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar cantidad");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/carrito";
    }

    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Long productoId,
                                 RedirectAttributes redirectAttributes) {
        try {
            carritoService.eliminarProducto(productoId);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar producto");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/carrito";
    }

    @PostMapping("/limpiar")
    public String limpiarCarrito(RedirectAttributes redirectAttributes) {
        try {
            carritoService.limpiarCarrito();
            redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al vaciar carrito");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/carrito";
    }

    // API endpoint para obtener cantidad del carrito (AJAX)
    @GetMapping("/api/cantidad")
    @ResponseBody
    public Integer obtenerCantidadCarrito() {
        return carritoService.obtenerCantidadTotal();
    }

    // API endpoint para agregar producto vía AJAX
    @PostMapping("/api/agregar")
    @ResponseBody
    public String agregarProductoAjax(@RequestParam Long productoId, 
                                    @RequestParam(defaultValue = "1") Integer cantidad) {
        try {
            carritoService.agregarProducto(productoId, cantidad);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    // API endpoint para eliminar producto vía AJAX
    @PostMapping("/api/eliminar")
    @ResponseBody
    public Map<String, Object> eliminarProductoAjax(@RequestParam Long productoId) {
        Map<String, Object> response = new HashMap<>();
        try {
            carritoService.eliminarProducto(productoId);
            response.put("success", true);
            response.put("message", "Producto eliminado del carrito");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar producto");
        }
        return response;
    }

    // API endpoint para validar stock de todos los items en el carrito
    @PostMapping("/api/validar")
    @ResponseBody
    public Map<String, Object> validarStockCarrito() {
        List<CarritoItem> items = carritoService.obtenerItems();
        List<Map<String, Object>> insuficientes = new ArrayList<>();

        for (CarritoItem ci : items) {
            Producto p = ci.getProducto();
            if (p == null) continue;
            int disponible = p.getStock() != null ? p.getStock() : 0;
            if (disponible < ci.getCantidad()) {
                Map<String, Object> info = new HashMap<>();
                info.put("productoId", p.getId());
                info.put("nombre", p.getNombre());
                info.put("solicitado", ci.getCantidad());
                info.put("disponible", disponible);
                insuficientes.add(info);
            }
        }

        Map<String, Object> resp = new HashMap<>();
        if (insuficientes.isEmpty()) {
            resp.put("ok", true);
        } else {
            resp.put("ok", false);
            resp.put("insuficientes", insuficientes);
        }
        return resp;
    }

    // Endpoint para eliminar productos sin stock y redirigir a productos
    @PostMapping("/eliminar-sin-stock")
    public String eliminarProductosSinStock(RedirectAttributes redirectAttributes) {
        try {
            List<CarritoItem> items = carritoService.obtenerItems();
            int eliminados = 0;
            
            for (CarritoItem ci : items) {
                Producto p = ci.getProducto();
                if (p == null) continue;
                int disponible = p.getStock() != null ? p.getStock() : 0;
                if (disponible < ci.getCantidad()) {
                    carritoService.eliminarProducto(p.getId());
                    eliminados++;
                }
            }
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Se eliminaron " + eliminados + " producto(s) sin stock del carrito");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar productos sin stock");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/productos";
    }
}