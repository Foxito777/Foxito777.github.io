// corregido por jesus laura ramos - Actualizado para JPA
package com.proyectoweb.Juledtoys.servicios;

import com.proyectoweb.Juledtoys.entidades.Producto;
import com.proyectoweb.Juledtoys.repositorios.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Service
public class ProductoService {
    private final ProductoRepository repo;
    public ProductoService(ProductoRepository repo) { this.repo = repo; }

    public List<Producto> listarTodos() { 
        return repo.findByDisponibleTrue(); 
    }

    public List<Producto> listarRecomendados(int limite) {
        Page<Producto> page = repo.findRandomProducts(PageRequest.of(0, limite));
        return page.getContent();
    }

    // Paginación usando Spring Data
    public Page<Producto> pagina(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        return repo.findByDisponibleTrue(pageable);
    }

    public long totalProductos() {
        return repo.count();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return repo.findByIdAndDisponibleTrue(id);
    }

    public List<Producto> buscarPorCategoria(String categoria, Long excluirId) {
        return repo.findByCategoriaAndIdNot(categoria, excluirId);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return repo.findByNombreContainingIgnoreCaseAndDisponibleTrue(nombre);
    }

    /**
     * Buscar con filtros opcionales: categoría, rango de precio y nombre.
     * Devuelve hasta 'limit' resultados (paginación simple en la capa service).
     */
    public List<Producto> buscarConFiltros(String categoria, java.math.BigDecimal precioMin, java.math.BigDecimal precioMax, String nombre, int limit) {
        // Usar el query definido en el repositorio con paginación
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, Math.max(1, limit));
        org.springframework.data.domain.Page<Producto> page = repo.buscarConFiltros(categoria, precioMin, precioMax, nombre, pageable);
        return page.getContent();
    }

    public Producto crear(Producto producto) {
        return repo.save(producto);
    }

    public boolean eliminar(Long id) {
        Optional<Producto> producto = repo.findById(id);
        if (producto.isPresent()) {
            producto.get().setDisponible(false);
            repo.save(producto.get());
            return true;
        }
        return false;
    }

    // Método para obtener productos similares
    public List<Producto> obtenerProductosSimilares(Long productId, int limit) {
        Optional<Producto> producto = repo.findById(productId);
        if (producto.isPresent()) {
            List<Producto> similares = repo.findByCategoriaAndIdNot(
                producto.get().getCategoria(), productId);
            return similares.size() > limit ? similares.subList(0, limit) : similares;
        }
    Page<Producto> page = repo.findRandomProducts(PageRequest.of(0, limit));
    return page.getContent();
    }

    // Método para obtener productos aleatorios para carrusel
    public List<Producto> obtenerProductosAleatorios(int limit) {
        Page<Producto> page = repo.findRandomProducts(PageRequest.of(0, limit));
        return page.getContent();
    }

    /**
     * Listar categorías distintas de productos disponibles.
     */
    public List<String> listarCategorias() {
        return repo.findDistinctCategorias();
    }

    public BigDecimal obtenerPrecioMinimo() {
        BigDecimal v = repo.findMinPrecio();
        return v != null ? v : BigDecimal.ZERO;
    }

    public BigDecimal obtenerPrecioMaximo() {
        BigDecimal v = repo.findMaxPrecio();
        return v != null ? v : BigDecimal.ZERO;
    }
}
