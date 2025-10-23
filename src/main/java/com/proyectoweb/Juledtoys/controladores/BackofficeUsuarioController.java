package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.entidades.Usuario;
import com.proyectoweb.Juledtoys.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador para gestión de usuarios en el backoffice
 * Solo accesible para usuarios con rol ADMIN
 */
@Controller
@RequestMapping("/backoffice/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class BackofficeUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Listar todos los usuarios
     */
    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("usuariosActivos", usuarios.stream().filter(Usuario::isActivo).count());
        return "backoffice/usuarios";
    }

    /**
     * Mostrar formulario para crear nuevo usuario
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Usuario usuario = new Usuario();
        usuario.setActivo(true);
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Usuario.Rol.values());
        model.addAttribute("accion", "Crear");
        return "backoffice/usuario-form";
    }

    /**
     * Mostrar formulario para editar usuario existente
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/backoffice/admin/usuarios";
        }
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Usuario.Rol.values());
        model.addAttribute("accion", "Editar");
        return "backoffice/usuario-form";
    }

    /**
     * Guardar usuario (crear o actualizar)
     */
    @PostMapping("/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario, 
                                 BindingResult result,
                                 @RequestParam(required = false) String nuevaPassword,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("roles", Usuario.Rol.values());
            model.addAttribute("accion", usuario.getId() == null ? "Crear" : "Editar");
            return "backoffice/usuario-form";
        }

        try {
            // Si es nuevo usuario o se proporciona nueva contraseña
            if (usuario.getId() == null) {
                // Validar que el username y email no existan
                if (usuarioService.existeUsername(usuario.getUsername())) {
                    model.addAttribute("error", "El nombre de usuario ya existe");
                    model.addAttribute("roles", Usuario.Rol.values());
                    model.addAttribute("accion", "Crear");
                    return "backoffice/usuario-form";
                }
                if (usuarioService.existeEmail(usuario.getEmail())) {
                    model.addAttribute("error", "El correo electrónico ya existe");
                    model.addAttribute("roles", Usuario.Rol.values());
                    model.addAttribute("accion", "Crear");
                    return "backoffice/usuario-form";
                }
                // Encriptar contraseña para nuevo usuario
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            } else {
                // Usuario existente - recuperar datos actuales
                Usuario usuarioExistente = usuarioService.obtenerPorId(usuario.getId());
                if (usuarioExistente == null) {
                    redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                    return "redirect:/backoffice/admin/usuarios";
                }
                
                // Actualizar solo los campos modificables
                usuarioExistente.setUsername(usuario.getUsername());
                usuarioExistente.setEmail(usuario.getEmail());
                usuarioExistente.setNombreCompleto(usuario.getNombreCompleto());
                usuarioExistente.setTelefono(usuario.getTelefono());
                usuarioExistente.setDireccion(usuario.getDireccion());
                usuarioExistente.setRol(usuario.getRol());
                usuarioExistente.setActivo(usuario.isActivo());
                
                // Si se proporciona nueva contraseña, actualizarla
                if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
                    if (nuevaPassword.length() < 6) {
                        model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                        model.addAttribute("roles", Usuario.Rol.values());
                        model.addAttribute("accion", "Editar");
                        model.addAttribute("usuario", usuario);
                        return "backoffice/usuario-form";
                    }
                    usuarioExistente.setPassword(passwordEncoder.encode(nuevaPassword));
                }
                
                usuario = usuarioExistente;
            }

            usuarioService.guardar(usuario);
            redirectAttributes.addFlashAttribute("success", 
                usuario.getId() == null ? "Usuario creado exitosamente" : "Usuario actualizado exitosamente");
            return "redirect:/backoffice/admin/usuarios";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar usuario: " + e.getMessage());
            model.addAttribute("roles", Usuario.Rol.values());
            model.addAttribute("accion", usuario.getId() == null ? "Crear" : "Editar");
            return "backoffice/usuario-form";
        }
    }

    /**
     * Eliminar usuario (soft delete - marcar como inactivo)
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.obtenerPorId(id);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/backoffice/admin/usuarios";
            }
            
            // Soft delete - marcar como inactivo
            usuario.setActivo(false);
            usuarioService.guardar(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Usuario desactivado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
    return "redirect:/backoffice/admin/usuarios";
    }

    /**
     * Activar usuario
     */
    @PostMapping("/activar/{id}")
    public String activarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.obtenerPorId(id);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/backoffice/admin/usuarios";
            }
            
            usuario.setActivo(true);
            usuarioService.guardar(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Usuario activado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al activar usuario: " + e.getMessage());
        }
    return "redirect:/backoffice/admin/usuarios";
    }

    /**
     * Eliminar múltiples usuarios seleccionados (borrado físico)
     */
    @PostMapping("/eliminar-multiple")
    public String eliminarUsuariosMultiple(@RequestParam(name = "usuarioIds", required = false) List<Long> usuarioIds,
                                          RedirectAttributes redirectAttributes) {
        if (usuarioIds == null || usuarioIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se seleccionaron usuarios para eliminar.");
            return "redirect:/backoffice/admin/usuarios";
        }
        int count = 0;
        for (Long id : usuarioIds) {
            try {
                usuarioService.eliminarPorId(id);
                count++;
            } catch (Exception e) {
                // Ignorar errores individuales para continuar con los demás
            }
        }
        if (count > 0) {
            redirectAttributes.addFlashAttribute("success", count + " usuario(s) eliminados exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar ningún usuario seleccionado.");
        }
        return "redirect:/backoffice/admin/usuarios";
    }

    /**
     * Cambiar rol de usuario
     */
    @PostMapping("/cambiar-rol/{id}")
    public String cambiarRol(@PathVariable Long id, 
                             @RequestParam Usuario.Rol nuevoRol,
                             RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.obtenerPorId(id);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/backoffice/admin/usuarios";
            }
            
            usuario.setRol(nuevoRol);
            usuarioService.guardar(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Rol actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar rol: " + e.getMessage());
        }
    return "redirect:/backoffice/admin/usuarios";
    }
}
