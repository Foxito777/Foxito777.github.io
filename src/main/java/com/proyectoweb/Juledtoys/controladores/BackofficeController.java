package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.entidades.Producto;
import com.proyectoweb.Juledtoys.repositorios.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

@Controller
@RequestMapping("/backoffice")
public class BackofficeController {
    private final ProductoRepository productoRepository;

    public BackofficeController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // READ - Mostrar todos los productos
    @GetMapping("/admin")
    public String adminPanel(@RequestParam(name = "q", required = false) String q, Model model) {
        var productos = (q != null && !q.trim().isEmpty()) ? productoRepository.findByNombreContainingIgnoreCaseAndDisponibleTrue(q) : productoRepository.findByDisponibleTrue();
        model.addAttribute("productos", productos);
        model.addAttribute("nuevoProducto", new Producto());

        // Calcular estadísticas
        long totalProductos = productos.size();
        int stockTotal = productos.stream().mapToInt(Producto::getStock).sum();
        BigDecimal valorTotalInventario = productos.stream()
                .map(p -> p.getPrecio().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long stockBajo = productos.stream().filter(p -> p.getStock() < 6).count();

        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("stockTotal", stockTotal);
        model.addAttribute("valorTotalInventario", valorTotalInventario);
        model.addAttribute("stockBajo", stockBajo);

        return "backoffice/admin";
    }

    // CREATE - Crear nuevo producto
    @PostMapping("/admin/productos")
    public String crearProducto(@ModelAttribute Producto producto,
                                RedirectAttributes redirectAttributes) {
        try {
            productoRepository.save(producto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto creado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al crear el producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/backoffice/admin";
    }

    // UPDATE - Actualizar producto existente
    @PostMapping("/admin/productos/{id}/editar")
    public String actualizarProducto(@PathVariable Long id,
                                     @ModelAttribute Producto producto,
                                     RedirectAttributes redirectAttributes) {
        try {
            Optional<Producto> productoExistente = productoRepository.findById(id);
            if (productoExistente.isPresent()) {
                Producto p = productoExistente.get();
                p.setNombre(producto.getNombre());
                p.setDescripcion(producto.getDescripcion());
                p.setPrecio(producto.getPrecio());
                p.setStock(producto.getStock());
                productoRepository.save(p);

                redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Producto no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar el producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/backoffice/admin";
    }

    // DELETE - Eliminar producto (eliminación suave)
    @PostMapping("/admin/productos/{id}/eliminar")
    public String eliminarProducto(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            Optional<Producto> producto = productoRepository.findById(id);
            if (producto.isPresent()) {
                Producto p = producto.get();
                p.setDisponible(false);
                productoRepository.save(p);
                redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Producto no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar el producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/backoffice/admin";
    }

    /**
     * Eliminar múltiples productos (eliminación física)
     */
    @PostMapping("/admin/productos/eliminar-multiple")
    public String eliminarProductosMultiple(@RequestParam(value = "productoIds", required = false) List<Long> productoIds,
                                            RedirectAttributes redirectAttributes) {
        if (productoIds == null || productoIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No se seleccionaron productos para eliminar");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/backoffice/admin";
        }
        try {
            for (Long id : productoIds) {
                productoRepository.deleteById(id);
            }
            redirectAttributes.addFlashAttribute("mensaje", "Productos eliminados exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar productos: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/backoffice/admin";
    }

    // GET - Obtener producto por ID (para el modal de edición)
    @GetMapping("/admin/productos/{id}")
    @ResponseBody
    public Producto obtenerProducto(@PathVariable Long id) {
        return productoRepository.findById(id).orElse(null);
    }
}