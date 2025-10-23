package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.servicios.CarritoServiceJPA;
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
        model.addAttribute("carrito", carritoService.obtenerItems());
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
}