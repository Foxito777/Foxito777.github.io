package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.entidades.Cliente;
import com.proyectoweb.Juledtoys.servicios.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador para gestión de clientes en el backoffice
 * Visualización: Todos los roles autenticados
 * Modificación: Solo rol ADMIN
 */
@Controller
@RequestMapping("/backoffice/admin/clientes")
public class BackofficeClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Listar todos los clientes (usuarios con rol CLIENTE)
     * Accesible para cualquier usuario autenticado
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String listarClientes(Model model) {
    List<Cliente> clientes = clienteService.listarTodos();
    model.addAttribute("clientes", clientes);
        model.addAttribute("totalClientes", clientes.size());
        model.addAttribute("clientesActivos", clientes.stream().filter(Cliente::isActivo).count());
        return "backoffice/clientes";
    }

    /**
     * Mostrar formulario para crear nuevo cliente
     * Solo accesible para ADMIN
     */
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String mostrarFormularioNuevo(Model model) {
        Cliente cliente = new Cliente();
        cliente.setActivo(true);
        model.addAttribute("cliente", cliente);
        model.addAttribute("accion", "Crear");
        model.addAttribute("soloLectura", false);
        return "backoffice/cliente-form";
    }

    /**
     * Mostrar formulario para editar cliente existente
     * ADMIN: puede editar
     * Otros roles: solo lectura
     */
    @GetMapping("/editar/{id}")
    @PreAuthorize("isAuthenticated()")
    public String mostrarFormularioEditar(@PathVariable Long id, 
                                          Model model, 
                                          RedirectAttributes redirectAttributes,
                                          @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        Cliente cliente = clienteService.buscarPorId(id).orElse(null);
        if (cliente == null) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/backoffice/admin/clientes";
        }
        
        // Verificar si el usuario tiene rol ADMIN
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        model.addAttribute("cliente", cliente);
        model.addAttribute("accion", isAdmin ? "Editar" : "Ver");
        model.addAttribute("soloLectura", !isAdmin);
        return "backoffice/cliente-form";
    }

    /**
     * Ver detalles del cliente (solo lectura)
     * Accesible para cualquier usuario autenticado
     */
    @GetMapping("/ver/{id}")
    @PreAuthorize("isAuthenticated()")
    public String verCliente(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Cliente cliente = clienteService.buscarPorId(id).orElse(null);
        if (cliente == null) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/backoffice/admin/clientes";
        }
        
        model.addAttribute("cliente", cliente);
        model.addAttribute("accion", "Ver");
        model.addAttribute("soloLectura", true);
        return "backoffice/cliente-form";
    }

    /**
     * Guardar cliente (crear o actualizar)
     * Solo accesible para ADMIN
     */
    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardarCliente(@Valid @ModelAttribute("cliente") Cliente cliente, 
                                 BindingResult result,
                                 @RequestParam(required = false) String nuevaPassword,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("accion", cliente.getId() == null ? "Crear" : "Editar");
            model.addAttribute("soloLectura", false);
            return "backoffice/cliente-form";
        }

        try {
            // Si es nuevo cliente
            if (cliente.getId() == null) {
                // Validar que el username y email no existan
                if (clienteService.buscarPorUsername(cliente.getUsername()).isPresent()) {
                    model.addAttribute("error", "El nombre de usuario ya existe");
                    model.addAttribute("accion", "Crear");
                    model.addAttribute("soloLectura", false);
                    return "backoffice/cliente-form";
                }
                if (clienteService.buscarPorEmail(cliente.getEmail()).isPresent()) {
                    model.addAttribute("error", "El correo electrónico ya existe");
                    model.addAttribute("accion", "Crear");
                    model.addAttribute("soloLectura", false);
                    return "backoffice/cliente-form";
                }
                // Encriptar contraseña para nuevo cliente
                cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
            } else {
                // Cliente existente - recuperar datos actuales
                Cliente clienteExistente = clienteService.buscarPorId(cliente.getId()).orElse(null);
                if (clienteExistente == null) {
                    redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
                    return "redirect:/backoffice/admin/clientes";
                }
                
                // Actualizar solo los campos modificables
                clienteExistente.setUsername(cliente.getUsername());
                clienteExistente.setEmail(cliente.getEmail());
                clienteExistente.setNombreCompleto(cliente.getNombreCompleto());
                clienteExistente.setTelefono(cliente.getTelefono());
                clienteExistente.setDireccion(cliente.getDireccion());
                clienteExistente.setActivo(cliente.isActivo());
                
                // Si se proporciona nueva contraseña, actualizarla
                if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
                    if (nuevaPassword.length() < 6) {
                        model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                        model.addAttribute("accion", "Editar");
                        model.addAttribute("soloLectura", false);
                        model.addAttribute("cliente", cliente);
                        return "backoffice/cliente-form";
                    }
                    clienteExistente.setPassword(passwordEncoder.encode(nuevaPassword));
                }
                
                cliente = clienteExistente;
            }

            boolean isNuevo = (cliente.getId() == null);
            clienteService.guardar(cliente);
            redirectAttributes.addFlashAttribute("success", 
                isNuevo ? "Cliente creado exitosamente" : "Cliente actualizado exitosamente");
            return "redirect:/backoffice/admin/clientes";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar cliente: " + e.getMessage());
            model.addAttribute("accion", cliente.getId() == null ? "Crear" : "Editar");
            model.addAttribute("soloLectura", false);
            return "backoffice/cliente-form";
        }
    }

    /**
     * Desactivar cliente (soft delete)
     * Solo accesible para ADMIN
     */
    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (!clienteService.buscarPorId(id).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
                return "redirect:/backoffice/admin/clientes";
            }
            // Eliminación física
            clienteService.eliminarPorId(id);
            redirectAttributes.addFlashAttribute("success", "Cliente eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar cliente: " + e.getMessage());
        }
        return "redirect:/backoffice/admin/clientes";
    }

    /**
     * Activar cliente
     * Solo accesible para ADMIN
     */
    // La activación/estado ya no se gestiona desde la UI; si es necesario, puede implementarse un endpoint
    // separado con controles adicionales. Por ahora se elimina el endpoint de activar para evitar confusión.
}
