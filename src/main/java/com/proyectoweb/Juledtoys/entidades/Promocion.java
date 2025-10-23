package com.proyectoweb.Juledtoys.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Entidad JPA para gestionar promociones del carrusel (Backoffice + Home).
 */
@Entity
@Table(name = "promociones")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Visual/Contenido
    @Size(max = 50, message = "La etiqueta no puede exceder 50 caracteres")
    @Column(length = 50)
    private String etiqueta; // Ej: "Mega Sale", "Nuevo"

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String titulo; // Título principal del slide

    @Size(max = 200, message = "El subtítulo no puede exceder 200 caracteres")
    @Column(length = 200)
    private String subtitulo; // Línea secundaria

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String descripcion; // Texto adicional (opcional)

    @Size(max = 50, message = "El texto CTA no puede exceder 50 caracteres")
    @Column(name = "cta_texto", length = 50)
    private String ctaTexto; // Texto del botón (CTA)

    @Size(max = 255, message = "La URL CTA no puede exceder 255 caracteres")
    @Column(name = "cta_url", length = 255)
    private String ctaUrl; // Enlace del botón (CTA)

    @Size(max = 255, message = "La ruta de imagen no puede exceder 255 caracteres")
    @Column(length = 255)
    private String imagen; // Ruta estática: /Imagenes/promociones/xxx.png

    // Estilos
    @Size(max = 7, message = "El color de fondo debe ser un código HEX válido")
    @Column(name = "color_fondo", length = 7)
    private String colorFondo; // HEX, ej. #ff6b35

    @Size(max = 7, message = "El color de texto debe ser un código HEX válido")
    @Column(name = "color_texto", length = 7)
    private String colorTexto; // HEX, ej. #ffffff

    // Estado/Reglas
    @Column(name = "fecha_inicio")
    private LocalDate inicio; // Fecha de inicio (inclusive)

    @Column(name = "fecha_fin")
    private LocalDate fin; // Fecha de fin (inclusive)

    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    @Column(length = 20)
    private String estado = "ACTIVA"; // ACTIVA | PROGRAMADA | FINALIZADA

    @Size(max = 10, message = "La prioridad no puede exceder 10 caracteres")
    @Column(length = 10)
    private String prioridad = "MEDIA"; // ALTA | MEDIA | BAJA

    @Column(name = "destacado")
    private boolean destacado = false; // Si aparece en el carrusel de Home

    @Column(name = "orden_presentacion")
    private int orden = 0; // Posición del slide (menor = primero)

    // Constructores
    public Promocion() {
    }

    public Promocion(String titulo) {
        this.titulo = titulo;
    }

    public Promocion(String etiqueta, String titulo, String subtitulo, String descripcion,
                     String ctaTexto, String ctaUrl, String imagen, String colorFondo,
                     String colorTexto, LocalDate inicio, LocalDate fin, String estado,
                     String prioridad, boolean destacado, int orden) {
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

    // Helpers de negocio
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

    // Getters y Setters
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