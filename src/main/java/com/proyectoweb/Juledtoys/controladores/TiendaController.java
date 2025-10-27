package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.servicios.CarritoServiceJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class TiendaController {

    @Autowired
    private CarritoServiceJPA carritoService;

    @GetMapping("/tienda")
    public String tienda(Model model) {
        // Agregar el conteo del carrito al modelo
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        
        // Agregar categorías disponibles
        model.addAttribute("categorias", obtenerCategorias());
        
        // Agregar productos destacados
        model.addAttribute("productosDestacados", obtenerProductosDestacados());
        
        return "tienda";
    }

    @GetMapping("/api/productos/filtrar")
    @ResponseBody
    public Map<String, Object> filtrarProductos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String precio,
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "relevancia") String ordenar) {
        
        List<Map<String, Object>> productos = obtenerProductosFiltrados(categoria, precio, busqueda, ordenar);
        
        Map<String, Object> response = new HashMap<>();
        response.put("productos", productos);
        response.put("total", productos.size());
        response.put("pagina", 1);
        response.put("totalPaginas", (int) Math.ceil(productos.size() / 12.0));
        
        return response;
    }

    @GetMapping("/api/producto/detalle")
    @ResponseBody
    public Map<String, Object> obtenerDetalleProducto(@RequestParam Long id) {
        return obtenerProductoPorId(id);
    }

    @GetMapping("/api/productos/relacionados")
    @ResponseBody
    public List<Map<String, Object>> obtenerProductosRelacionados(@RequestParam Long id) {
        return obtenerProductosRelacionadosPorId(id);
    }

    // Métodos auxiliares para simular datos
    private List<String> obtenerCategorias() {
        return Arrays.asList(
            "LEGO",
            "Star Wars",
            "Marvel",
            "DC Comics",
            "Anime",
            "Vehículos",
            "Figuras de Acción",
            "Construcción",
            "Coleccionables"
        );
    }

    private List<Map<String, Object>> obtenerProductosDestacados() {
        List<Map<String, Object>> productos = new ArrayList<>();
        
        // Producto 1
        Map<String, Object> producto1 = new HashMap<>();
        producto1.put("id", 1L);
        producto1.put("nombre", "LEGO Creator Mansion Encantada");
        producto1.put("precio", 899.99);
        producto1.put("precioAnterior", 1199.99);
        producto1.put("descuento", 25);
        producto1.put("imagen", "Imagenes/Mansion.png");
        producto1.put("categoria", "LEGO");
        producto1.put("stock", 15);
        producto1.put("nuevo", true);
        producto1.put("calificacion", 4.8);
        producto1.put("vendidos", 127);
        productos.add(producto1);

        // Producto 2
        Map<String, Object> producto2 = new HashMap<>();
        producto2.put("id", 2L);
        producto2.put("nombre", "Caza Estelar X-Wing Star Wars");
        producto2.put("precio", 459.99);
        producto2.put("precioAnterior", 599.99);
        producto2.put("descuento", 23);
        producto2.put("imagen", "Imagenes/Caza.png");
        producto2.put("categoria", "Star Wars");
        producto2.put("stock", 8);
        producto2.put("nuevo", false);
        producto2.put("calificacion", 4.9);
        producto2.put("vendidos", 89);
        productos.add(producto2);

        // Producto 3
        Map<String, Object> producto3 = new HashMap<>();
        producto3.put("id", 3L);
        producto3.put("nombre", "Ferrari F8 Tributo Speed");
        producto3.put("precio", 299.99);
        producto3.put("precioAnterior", 399.99);
        producto3.put("descuento", 25);
        producto3.put("imagen", "Imagenes/Ferrari.png");
        producto3.put("categoria", "Vehículos");
        producto3.put("stock", 22);
        producto3.put("nuevo", true);
        producto3.put("calificacion", 4.7);
        producto3.put("vendidos", 156);
        productos.add(producto3);

        // Agregar más productos...
        for (int i = 4; i <= 12; i++) {
            Map<String, Object> producto = new HashMap<>();
            producto.put("id", (long) i);
            producto.put("nombre", "Producto Destacado " + i);
            producto.put("precio", 199.99 + (i * 50));
            producto.put("precioAnterior", 299.99 + (i * 50));
            producto.put("descuento", 15 + (i % 20));
            producto.put("imagen", "Imagenes/producto" + i + ".png");
            producto.put("categoria", obtenerCategorias().get(i % obtenerCategorias().size()));
            producto.put("stock", 10 + (i % 20));
            producto.put("nuevo", i % 3 == 0);
            producto.put("calificacion", 4.2 + (i % 8) * 0.1);
            producto.put("vendidos", 50 + (i * 15));
            productos.add(producto);
        }

        return productos;
    }

    private List<Map<String, Object>> obtenerProductosFiltrados(String categoria, String precio, String busqueda, String ordenar) {
        List<Map<String, Object>> todosLosProductos = obtenerProductosDestacados();
        
        // Aplicar filtros
        // Stream.toList() returns an unmodifiable list; necesitamos una lista modificable
        List<Map<String, Object>> productosFiltrados = new ArrayList<>(
            todosLosProductos.stream()
                .filter(p -> categoria == null || categoria.isEmpty() || p.get("categoria").equals(categoria))
                .filter(p -> busqueda == null || busqueda.isEmpty() || 
                    ((String) p.get("nombre")).toLowerCase().contains(busqueda.toLowerCase()))
                .toList()
        );

        // Aplicar ordenamiento
        switch (ordenar) {
            case "precio_asc":
                productosFiltrados.sort(Comparator.comparing(p -> (Double) p.get("precio")));
                break;
            case "precio_desc":
                productosFiltrados.sort(Comparator.comparing((Map<String, Object> p) -> (Double) p.get("precio")).reversed());
                break;
            case "nombre":
                productosFiltrados.sort(Comparator.comparing(p -> (String) p.get("nombre")));
                break;
            case "calificacion":
                productosFiltrados.sort(Comparator.comparing((Map<String, Object> p) -> (Double) p.get("calificacion")).reversed());
                break;
            default: // relevancia
                productosFiltrados.sort(Comparator.comparing((Map<String, Object> p) -> (Integer) p.get("vendidos")).reversed());
        }

        return productosFiltrados;
    }

    private Map<String, Object> obtenerProductoPorId(Long id) {
        return obtenerProductosDestacados().stream()
            .filter(p -> p.get("id").equals(id))
            .findFirst()
            .orElse(new HashMap<>());
    }

    private List<Map<String, Object>> obtenerProductosRelacionadosPorId(Long id) {
        Map<String, Object> producto = obtenerProductoPorId(id);
        String categoria = (String) producto.get("categoria");
        
        return obtenerProductosDestacados().stream()
            .filter(p -> !p.get("id").equals(id))
            .filter(p -> p.get("categoria").equals(categoria))
            .limit(4)
            .toList();
    }
}