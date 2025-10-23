// corregido por jesus laura ramos - Actualizado para JPA
package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.entidades.Producto;
import com.proyectoweb.Juledtoys.servicios.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://127.0.0.1:5500")
// permite que tu frontend desde Live Server acceda al backend
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // Listar todos
    @GetMapping
    public ResponseEntity<List<Producto>> getAll() {
        List<Producto> productos = service.listarTodos();
        return ResponseEntity.ok(productos);
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Agregar producto
    @PostMapping
    public ResponseEntity<Producto> create(@RequestBody Producto producto) {
        Producto creado = service.crear(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean eliminado = service.eliminar(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Catalogo de productos
    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "12") int size,
                           Model model) {

        long total = service.totalProductos();
        int totalPaginas = (int) Math.ceil((double) total / size);

        Page<Producto> paginaProductos = service.pagina(page, size);
        model.addAttribute("productos", paginaProductos.getContent());
        model.addAttribute("productosCarrusel", service.listarRecomendados(12));
        model.addAttribute("categorias",
                service.listarTodos().stream().map(p -> p.getCategoria()).distinct().toList());

        model.addAttribute("totalProductos", total);
        model.addAttribute("paginaActual", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPaginas", totalPaginas);

        return "productos";
    }

    // Mostrar productos
    @GetMapping("/productos")
    public String productos(Model model) {
        List<Producto> productos = service.listarTodos();
        
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        
        // Para el carrusel
        model.addAttribute("productosCarrusel", service.listarRecomendados(12));
        
        // Para los filtros
        Set<String> categorias = productos.stream()
            .map(Producto::getCategoria)
            .collect(Collectors.toSet());
        model.addAttribute("categorias", categorias);
        
        return "productos";
    }

    // Mostrar detalle de un producto específico
    @GetMapping("/producto/{id}")
    public String detalleProducto(@PathVariable Long id, Model model) {
        Optional<Producto> producto = service.buscarPorId(id);
        
        if (producto.isEmpty()) {
            return "redirect:/productos"; // Si no existe, volver al catálogo
        }
        
        model.addAttribute("producto", producto.get());
        
        // Productos relacionados (misma categoría)
        List<Producto> relacionados = service.buscarPorCategoria(producto.get().getCategoria(), id);
        model.addAttribute("productosRelacionados", relacionados);
        
        return "detalle-producto";
    }
}
