package com.proyectoweb.Juledtoys.modelos_old;

public class ItemCarrito {
    private Long productoId;
    private String nombre;
    private Double precio;
    private Integer cantidad;
    private String imagenUrl;

    public ItemCarrito() {}

    public ItemCarrito(Long productoId, String nombre, Double precio, Integer cantidad, String imagenUrl) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagenUrl = imagenUrl;
    }

    // Método para calcular subtotal
    public Double getSubtotal() {
        return precio * cantidad;
    }

    // Getters y Setters
    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}