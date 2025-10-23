package com.proyectoweb.Juledtoys.servicios;

import com.proyectoweb.Juledtoys.entidades.Producto;
import com.proyectoweb.Juledtoys.repositorios.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoServiceJPA {

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Crear nuevo producto
     */
    public Producto crearProducto(Producto producto) {
        if (productoRepository.existsByNombreIgnoreCase(producto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un producto con ese nombre");
        }
        return productoRepository.save(producto);
    }

    /**
     * Buscar producto por ID
     */
    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    /**
     * Buscar producto por ID y que esté disponible
     */
    public Optional<Producto> buscarPorIdDisponible(Long id) {
        return productoRepository.findByIdAndDisponibleTrue(id);
    }

    /**
     * Obtener todos los productos disponibles
     */
    public List<Producto> obtenerProductosDisponibles() {
        return productoRepository.findByDisponibleTrue();
    }

    /**
     * Obtener productos por categoría
     */
    public List<Producto> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoriaAndDisponibleTrue(categoria);
    }

    /**
     * Buscar productos por nombre
     */
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndDisponibleTrue(nombre);
    }

    /**
     * Buscar productos por rango de precios
     */
    public List<Producto> buscarPorRangoPrecios(BigDecimal precioMin, BigDecimal precioMax) {
        return productoRepository.findByPrecioBetweenAndDisponibleTrue(precioMin, precioMax);
    }

    /**
     * Obtener productos con stock
     */
    public List<Producto> obtenerConStock() {
        return productoRepository.findByStockGreaterThanAndDisponibleTrue(0);
    }

    /**
     * Obtener productos con paginación
     */
    public Page<Producto> obtenerProductosPaginados(int pagina, int tamaño) {
        Pageable pageable = PageRequest.of(pagina, tamaño, Sort.by("fechaCreacion").descending());
        return productoRepository.findByDisponibleTrue(pageable);
    }

    /**
     * Obtener productos por categoría con paginación
     */
    public Page<Producto> obtenerPorCategoriaPaginados(String categoria, int pagina, int tamaño) {
        Pageable pageable = PageRequest.of(pagina, tamaño, Sort.by("fechaCreacion").descending());
        return productoRepository.findByCategoriaAndDisponibleTrue(categoria, pageable);
    }

    /**
     * Buscar productos con filtros
     */
    public Page<Producto> buscarConFiltros(String categoria, BigDecimal precioMin, BigDecimal precioMax, 
                                         String nombre, int pagina, int tamaño) {
        Pageable pageable = PageRequest.of(pagina, tamaño, Sort.by("fechaCreacion").descending());
        return productoRepository.buscarConFiltros(categoria, precioMin, precioMax, nombre, pageable);
    }

    /**
     * Obtener categorías disponibles
     */
    public List<String> obtenerCategorias() {
        return productoRepository.findDistinctCategorias();
    }

    /**
     * Obtener productos más recientes
     */
    public List<Producto> obtenerMasRecientes() {
        return productoRepository.findTop10ByDisponibleTrueOrderByFechaCreacionDesc();
    }

    /**
     * Obtener productos con stock bajo
     */
    public List<Producto> obtenerConStockBajo(int stockMinimo) {
        return productoRepository.findByStockLessThanAndDisponibleTrue(stockMinimo);
    }

    /**
     * Actualizar producto
     */
    public Producto actualizarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    /**
     * Cambiar disponibilidad del producto
     */
    public void cambiarDisponibilidad(Long id, boolean disponible) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        producto.setDisponible(disponible);
        productoRepository.save(producto);
    }

    /**
     * Actualizar stock
     */
    public void actualizarStock(Long id, int nuevoStock) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
    }

    /**
     * Reducir stock (para ventas)
     */
    public void reducirStock(Long id, int cantidad) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        
        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        
        producto.reducirStock(cantidad);
        productoRepository.save(producto);
    }

    /**
     * Eliminar producto (marcar como no disponible)
     */
    public void eliminarProducto(Long id) {
        cambiarDisponibilidad(id, false);
    }

    /**
     * Eliminar producto definitivamente
     */
    public void eliminarProductoDefinitivamente(Long id) {
        productoRepository.deleteById(id);
    }

    /**
     * Verificar si existe producto por nombre
     */
    public boolean existePorNombre(String nombre) {
        return productoRepository.existsByNombreIgnoreCase(nombre);
    }

    /**
     * Contar productos disponibles
     */
    public long contarProductosDisponibles() {
        return productoRepository.findByDisponibleTrue().size();
    }
}