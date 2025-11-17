package com.proyectoweb.Juledtoys.dto;

import org.springframework.web.multipart.MultipartFile;

public class YapeComprobanteDTO {
    private String numeroPedido;
    private String codigoOperacion;
    private String numeroTelefono;
    private MultipartFile comprobante;

    public YapeComprobanteDTO() {
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getCodigoOperacion() {
        return codigoOperacion;
    }

    public void setCodigoOperacion(String codigoOperacion) {
        this.codigoOperacion = codigoOperacion;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public MultipartFile getComprobante() {
        return comprobante;
    }

    public void setComprobante(MultipartFile comprobante) {
        this.comprobante = comprobante;
    }
}
