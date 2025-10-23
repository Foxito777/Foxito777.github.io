package com.proyectoweb.Juledtoys.modelos_old;

import java.time.LocalDate;

/**
 * POJO para gestionar promociones del carrusel (Backoffice + Home).
 * Mantiene compatibilidad con la arquitectura actual (in-memory).
 */
public class Promocion {

    private Long id;

    // Visual/Contenido
    private String etiqueta; // Ej: "Mega Sale", "Nuevo"
    private String titulo; // Título principal del slide
    private String subtitulo; // Línea secundaria
    private String descripcion; // Texto adicional (opcional)
    private String ctaTexto; // Texto del botón (CTA)
    private String ctaUrl; // Enlace del botón (CTA)
    private String imagen; // Ruta estática: /Imagenes/promociones/xxx.png

    // Estilos
    private String colorFondo; // HEX, ej. #ff6b35
    private String colorTexto; // HEX, ej. #ffffff

    // Estado/Reglas
    private LocalDate inicio; // Fecha de inicio (inclusive)
    private LocalDate fin; // Fecha de fin (inclusive)
    private String estado; // ACTIVA | PROGRAMADA | FINALIZADA
    private String prioridad; // ALTA | MEDIA | BAJA
    private boolean destacado; // Si aparece en el carrusel de Home
    private int orden; // Posición del slide (menor = primero)

    public Promocion() {
    }

    public Promocion(Long id,
            String etiqueta,
            String titulo,
            String subtitulo,
            String descripcion,
            String ctaTexto,
            String ctaUrl,
            String imagen,
            String colorFondo,
            String colorTexto,
            LocalDate inicio,
            LocalDate fin,
            String estado,
            String prioridad,
            boolean destacado,
            int orden) {
        this.id = id;
        this.etiqueta = etiqueta;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.descripcion = descripcion;
        this.ctaTexto = ctaTexto;
        this.ctaUrl = ctaUrl;
        this.imagen = imagen;
        this.colorFondo = colorFondo;
        this.colorTexto = colorTexto;
        this.inicio = inicio;
        this.fin = fin;
        this.estado = estado;
        this.prioridad = prioridad;
        this.destacado = destacado;
        this.orden = orden;
    }

    // --------- Helpers de negocio ---------

    public boolean estaActivaHoy(LocalDate hoy) {
        if (hoy == null)
            return false;
        boolean empiezaAntesOIgual = (inicio == null) || !inicio.isAfter(hoy);
        boolean terminaDespuesOIgual = (fin == null) || !fin.isBefore(hoy);
        return empiezaAntesOIgual && terminaDespuesOIgual
                && equalsIgnoreCase(estado, "ACTIVA");
    }

    public boolean estaProgramada(LocalDate hoy) {
        return hoy != null
                && inicio != null
                && inicio.isAfter(hoy)
                && equalsIgnoreCase(estado, "PROGRAMADA");
    }

    public boolean estaFinalizada(LocalDate hoy) {
        return (hoy != null && fin != null && fin.isBefore(hoy))
                || equalsIgnoreCase(estado, "FINALIZADA");
    }

    private boolean equalsIgnoreCase(String a, String b) {
        return a != null && a.equalsIgnoreCase(b);
    }

    // --------- Getters & Setters ---------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCtaTexto() {
        return ctaTexto;
    }

    public void setCtaTexto(String ctaTexto) {
        this.ctaTexto = ctaTexto;
    }

    public String getCtaUrl() {
        return ctaUrl;
    }

    public void setCtaUrl(String ctaUrl) {
        this.ctaUrl = ctaUrl;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getColorFondo() {
        return colorFondo;
    }

    public void setColorFondo(String colorFondo) {
        this.colorFondo = colorFondo;
    }

    public String getColorTexto() {
        return colorTexto;
    }

    public void setColorTexto(String colorTexto) {
        this.colorTexto = colorTexto;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public LocalDate getFin() {
        return fin;
    }

    public void setFin(LocalDate fin) {
        this.fin = fin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public boolean isDestacado() {
        return destacado;
    }

    public void setDestacado(boolean destacado) {
        this.destacado = destacado;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    // --------- toString (útil para logs) ---------

    @Override
    public String toString() {
        return "Promocion{" +
                "id=" + id +
                ", etiqueta='" + etiqueta + '\'' +
                ", titulo='" + titulo + '\'' +
                ", inicio=" + inicio +
                ", fin=" + fin +
                ", estado='" + estado + '\'' +
                ", prioridad='" + prioridad + '\'' +
                ", destacado=" + destacado +
                ", orden=" + orden +
                '}';
    }
}
