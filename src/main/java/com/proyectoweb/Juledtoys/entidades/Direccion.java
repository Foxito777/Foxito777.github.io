package com.proyectoweb.Juledtoys.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "direcciones")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos estructurados (opcionalmente puedes usarlos o dejar solo direccionCompleta)
    @Column(length = 100)
    private String departamento;

    @Column(length = 100)
    private String provincia;

    @Column(length = 100)
    private String distrito;

    @Size(max = 300)
    @Column(name = "detalle", length = 300)
    private String detalle;

    @Column(length = 50)
    private String numero;

    @Column(length = 30)
    private String telefono;

    // Campo de compatibilidad/rápido uso
    @Size(max = 500)
    @Column(name = "direccion_completa", length = 500)
    private String direccionCompleta;

    // Relación ManyToOne: varias direcciones pueden pertenecer a un mismo cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccionCompleta() { return direccionCompleta; }
    public void setDireccionCompleta(String direccionCompleta) { this.direccionCompleta = direccionCompleta; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    @Override
    public String toString() {
        if (direccionCompleta != null && !direccionCompleta.isBlank()) return direccionCompleta;
        StringBuilder sb = new StringBuilder();
        if (detalle != null) sb.append(detalle).append(" ");
        if (numero != null) sb.append(numero).append(" ");
        if (distrito != null) sb.append(distrito).append(" ");
        if (provincia != null) sb.append(provincia).append(" ");
        if (departamento != null) sb.append(departamento);
        return sb.toString().trim();
    }
}
