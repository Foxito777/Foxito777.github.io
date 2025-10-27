package com.proyectoweb.Juledtoys.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente implements org.springframework.security.core.userdetails.UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    // La validación de la contraseña se gestiona en el controlador para permitir
    // editar otros campos sin necesidad de reingresar la contraseña.
    @Column(nullable = false)
    private String password;

    @Size(max = 100)
    @Column(name = "nombre_completo", length = 100)
    private String nombreCompleto;

    @Size(max = 15)
    @Column(length = 15)
    private String telefono;

    // Ahora guardamos las direcciones en una entidad separada `Direccion`.
    // La relación es OneToMany: un cliente puede tener múltiples direcciones.
    // CascadeType.ALL + orphanRemoval=true asegura que al eliminar el cliente
    // o eliminar direcciones de la colección, JPA persista/elimine correctamente.
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Direccion> direcciones = new java.util.ArrayList<>();

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    public void actualizarUltimoAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    // Compatibilidad: mantener métodos get/set que usan String para evitar cambios mayores
    // en plantillas y controladores existentes. Internamente usamos la colección de Direcciones
    // y asumimos la primera como 'dirección principal'.
    public String getDireccion() {
        if (direcciones != null && !direcciones.isEmpty() && direcciones.get(0) != null) {
            return direcciones.get(0).getDireccionCompleta();
        }
        return null;
    }

    public void setDireccion(String direccionTexto) {
        if (direccionTexto == null) {
            if (this.direcciones != null) this.direcciones.clear();
            return;
        }
        if (this.direcciones == null) this.direcciones = new java.util.ArrayList<>();
        if (this.direcciones.isEmpty()) {
            Direccion d = new Direccion();
            d.setDireccionCompleta(direccionTexto);
            d.setCliente(this);
            this.direcciones.add(d);
        } else {
            Direccion d = this.direcciones.get(0);
            d.setDireccionCompleta(direccionTexto);
            d.setCliente(this);
        }
    }

    // Acceso directo a la entidad Dirección (principal) cuando se necesite manipular campos separados
    public Direccion getDireccionEntity() {
        return (direcciones != null && !direcciones.isEmpty()) ? direcciones.get(0) : null;
    }

    public void setDireccionEntity(Direccion direccion) {
        if (direccion == null) {
            if (this.direcciones != null) this.direcciones.clear();
            return;
        }
        direccion.setCliente(this);
        if (this.direcciones == null) this.direcciones = new java.util.ArrayList<>();
        if (this.direcciones.isEmpty()) this.direcciones.add(direccion);
        else this.direcciones.set(0, direccion);
    }

    // Acceso a la colección completa
    public java.util.List<Direccion> getDirecciones() { return direcciones; }
    public void setDirecciones(java.util.List<Direccion> direcciones) {
        this.direcciones = direcciones;
        if (this.direcciones != null) {
            for (Direccion d : this.direcciones) d.setCliente(this);
        }
    }

    public Boolean getActivo() { return activo; }
    public boolean isActivo() { return activo != null && activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    // UserDetails implementation
    @Override
    public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        return java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CLIENTE"));
    }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return isActivo(); }
}
