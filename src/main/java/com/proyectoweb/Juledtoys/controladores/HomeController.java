// corregido por jesus laura ramos - Actualizado para JPA
package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.entidades.Producto;
import com.proyectoweb.Juledtoys.servicios.ProductoService;
import com.proyectoweb.Juledtoys.servicios.CarritoServiceJPA;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final ProductoService productoService;
    private final CarritoServiceJPA carritoService;

    public HomeController(ProductoService productoService, CarritoServiceJPA carritoService) {
        this.productoService = productoService;
        this.carritoService = carritoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        // Agregar productos destacados al carrusel de la página principal
        List<Producto> productosDestacados = productoService.obtenerProductosAleatorios(6);
        model.addAttribute("productosDestacados", productosDestacados);
        
        // Información del carrito
        model.addAttribute("cantidadCarrito", carritoService.obtenerCantidadTotal());
        
        return "index"; // templates/index.html
    }

    @GetMapping("/productos")
    public String productos(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "categoria", required = false) String categoria,
            @RequestParam(name = "precioMin", required = false) String precioMinStr,
            @RequestParam(name = "precioMax", required = false) String precioMaxStr,
            Model model) {

        java.math.BigDecimal precioMin = null;
        java.math.BigDecimal precioMax = null;
        try {
            if (precioMinStr != null && !precioMinStr.isBlank()) {
                precioMin = new java.math.BigDecimal(precioMinStr.trim());
            }
            if (precioMaxStr != null && !precioMaxStr.isBlank()) {
                precioMax = new java.math.BigDecimal(precioMaxStr.trim());
            }
        } catch (NumberFormatException e) {
            // Ignorar formato inválido y dejar nulls
        }

        List<Producto> productos;
        // Si hay algún filtro (categoria o precio) o búsqueda q, usar la consulta con filtros
        if ((q != null && !q.trim().isEmpty()) || (categoria != null && !categoria.isBlank()) || precioMin != null || precioMax != null) {
            productos = productoService.buscarConFiltros(categoria, precioMin, precioMax, q, 1000);
        } else {
            productos = productoService.listarTodos();
        }

        model.addAttribute("productos", productos);

        // Productos para carrusel de la página productos
        List<Producto> productosCarrusel = productoService.obtenerProductosAleatorios(8);
        model.addAttribute("productosCarrusel", productosCarrusel);

        // Información del carrito
        model.addAttribute("cantidadCarrito", carritoService.obtenerCantidadTotal());

        // Categorías disponibles
    model.addAttribute("categorias", productoService.listarCategorias());

        // Precios globales (min/max) para el slider
        java.math.BigDecimal precioGlobalMin = productoService.obtenerPrecioMinimo();
        java.math.BigDecimal precioGlobalMax = productoService.obtenerPrecioMaximo();
        model.addAttribute("precioMinGlobal", precioGlobalMin != null ? precioGlobalMin.toPlainString() : "0");
        model.addAttribute("precioMaxGlobal", precioGlobalMax != null ? precioGlobalMax.toPlainString() : "0");

        // Pasar valores seleccionados para mantener el estado del formulario
        model.addAttribute("selectedCategoria", categoria);
        model.addAttribute("precioMin", precioMin != null ? precioMin.toPlainString() : "");
        model.addAttribute("precioMax", precioMax != null ? precioMax.toPlainString() : "");
        model.addAttribute("q", q);

        return "productos"; // templates/productos.html
    }

    @GetMapping("/producto/{id}")
    public String detalleProducto(@PathVariable Long id, Model model) {
        Optional<Producto> producto = productoService.buscarPorId(id);
        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            
            // Obtener productos similares para el carrusel
            List<Producto> productosSimilares = productoService.obtenerProductosSimilares(id, 6);
            model.addAttribute("productosSimilares", productosSimilares);
            
            // Información del carrito
            model.addAttribute("cantidadCarrito", carritoService.obtenerCantidadTotal());
            
            return "detalle-producto"; // templates/detalle-producto.html
        }
        return "redirect:/productos";
    }

    @GetMapping("/accesorios")
    public String accesorios(Model model) {
        // Información del carrito
        model.addAttribute("cantidadCarrito", carritoService.obtenerCantidadTotal());
        return "accesorios";
    }

    @GetMapping("/acerca")
    public String acerca(Model model) {
        // Información del carrito
        model.addAttribute("cantidadCarrito", carritoService.obtenerCantidadTotal());
        return "acerca";
    }

}
