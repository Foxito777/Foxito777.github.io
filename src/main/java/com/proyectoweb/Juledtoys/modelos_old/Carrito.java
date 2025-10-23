package com.proyectoweb.Juledtoys.modelos_old;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Carrito {
    private List<ItemCarrito> items;

    public Carrito() {
        this.items = new ArrayList<>();
    }

    // Agregar producto al carrito
    public void agregarProducto(Producto producto, Integer cantidad) {
        Optional<ItemCarrito> itemExistente = items.stream()
                .filter(item -> item.getProductoId().equals(producto.getId()))
                .findFirst();

        if (itemExistente.isPresent()) {
            // Si el producto ya existe, actualizar cantidad
            ItemCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
        } else {
            // Si no existe, crear nuevo item
            ItemCarrito nuevoItem = new ItemCarrito(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    cantidad,
                    producto.getImagenUrl()
            );
            items.add(nuevoItem);
        }
    }

    // Actualizar cantidad de un producto
    public void actualizarCantidad(Long productoId, Integer nuevaCantidad) {
        items.stream()
                .filter(item -> item.getProductoId().equals(productoId))
                .findFirst()
                .ifPresent(item -> item.setCantidad(nuevaCantidad));
    }

    // Eliminar producto del carrito
    public void eliminarProducto(Long productoId) {
        items.removeIf(item -> item.getProductoId().equals(productoId));
    }

    // Calcular total del carrito
    public Double getTotal() {
        return items.stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
    }

    // Obtener cantidad total de items
    public Integer getCantidadTotal() {
        return items.stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }

    // Limpiar carrito
    public void limpiar() {
        items.clear();
    }

    // Verificar si está vacío
    public boolean estaVacio() {
        return items.isEmpty();
    }

    // Getters y Setters
    public List<ItemCarrito> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrito> items) {
        this.items = items;
    }
}