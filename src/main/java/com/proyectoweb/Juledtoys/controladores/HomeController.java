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
    public String productos(@RequestParam(name = "q", required = false) String q, Model model) {
        // Listar todos los productos o filtrar por búsqueda
        List<Producto> productos = (q != null && !q.trim().isEmpty()) ? productoService.buscarPorNombre(q) : productoService.listarTodos();
        model.addAttribute("productos", productos);

        // Productos para carrusel de la página productos
        List<Producto> productosCarrusel = productoService.obtenerProductosAleatorios(8);
        model.addAttribute("productosCarrusel", productosCarrusel);

        // Información del carrito
        model.addAttribute("cantidadCarrito", carritoService.obtenerCantidadTotal());

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
