package com.proyectoweb.Juledtoys.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email no válido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    @Size(max = 100, message = "El nombre completo no puede exceder 100 caracteres")
    @Column(name = "nombre_completo", length = 100)
    private String nombreCompleto;

    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    @Column(length = 15)
    private String telefono;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(length = 200)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    // Enum para roles
    public enum Rol {
        ADMIN, VENDEDOR, CAJERO, MARKETING
    }

    // Constructor por defecto
    public Usuario() {
    }

    // Constructor principal
    public Usuario(String username, String email, String password, String nombreCompleto) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
    }

    // Métodos lifecycle de JPA
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    // Implementación de UserDetails para Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (rol == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    // Métodos de conveniencia para nombre y apellido
    public void setNombre(String nombre) {
        // Si ya hay un nombre completo, actualizar solo la primera parte
        if (this.nombreCompleto != null && !this.nombreCompleto.isEmpty()) {
            String[] partes = this.nombreCompleto.split(" ", 2);
            if (partes.length > 1) {
                this.nombreCompleto = nombre + " " + partes[1];
            } else {
                this.nombreCompleto = nombre;
            }
        } else {
            this.nombreCompleto = nombre;
        }
    }

    public void setApellido(String apellido) {
        // Si ya hay un nombre completo, actualizar solo la segunda parte
        if (this.nombreCompleto != null && !this.nombreCompleto.isEmpty()) {
            String[] partes = this.nombreCompleto.split(" ", 2);
            this.nombreCompleto = partes[0] + " " + apellido;
        } else {
            this.nombreCompleto = " " + apellido;
        }
    }

    public String getNombre() {
        if (nombreCompleto != null && !nombreCompleto.isEmpty()) {
            return nombreCompleto.split(" ")[0];
        }
        return "";
    }

    public String getApellido() {
        if (nombreCompleto != null && !nombreCompleto.isEmpty()) {
            String[] partes = nombreCompleto.split(" ", 2);
            return partes.length > 1 ? partes[1] : "";
        }
        return "";
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }
    
    public boolean isActivo() {
        return activo != null && activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    // Métodos de utilidad
    public boolean esAdmin() {
        return Rol.ADMIN.equals(this.rol);
    }

    public void actualizarUltimoAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", rol=" + rol +
                ", activo=" + activo +
                '}';
    }
}