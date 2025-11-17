package com.proyectoweb.Juledtoys.entidades;

public enum EstadoPedido {
    PENDIENTE("Pendiente", "warning"),
    PENDIENTE_PAGO("Pendiente de Pago", "warning"),
    CONFIRMADO("Confirmado", "info"),
    EN_PREPARACION("En Preparación", "primary"),
    EN_CAMINO("En Camino", "primary"),
    ENTREGADO("Entregado", "success"),
    CANCELADO("Cancelado", "danger");

    private final String descripcion;
    private final String badgeClass;

    EstadoPedido(String descripcion, String badgeClass) {
        this.descripcion = descripcion;
        this.badgeClass = badgeClass;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
