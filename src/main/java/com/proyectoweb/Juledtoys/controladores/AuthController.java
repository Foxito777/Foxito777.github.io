package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.entidades.Cliente;
import com.proyectoweb.Juledtoys.servicios.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import com.proyectoweb.Juledtoys.servicios.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

  @Autowired
  private ClienteService clienteService;

  @Autowired
  private AuthenticationConfiguration authenticationConfiguration;

  @Autowired
  private UsuarioService usuarioService;

  @GetMapping("/login")
  public String loginPage() {
    return "login"; // templates/login.html
  }
  
  @GetMapping("/register")
    public String registerPage() {
        return "register"; // templates/register.html
    }
  
  @PostMapping("/register")
  public String handleRegister(
      @RequestParam("firstName") String firstName,
      @RequestParam("lastName") String lastName,
      @RequestParam("email") String email,
      @RequestParam("password") String password,
      @RequestParam("confirmPassword") String confirmPassword,
      @RequestParam(value = "telefono", required = false) String telefono,
      @RequestParam(value = "direccion", required = false) String direccion,
      RedirectAttributes redirectAttributes,
      HttpServletRequest request
  ) {
    try {
      // Validaciones básicas del lado servidor
      if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
        redirectAttributes.addFlashAttribute("errorMessage", "Nombre y apellido son obligatorios");
        return "redirect:/register?error";
      }
      if (email == null || email.isBlank()) {
        redirectAttributes.addFlashAttribute("errorMessage", "El correo electrónico es obligatorio");
        return "redirect:/register?error";
      }
      if (password == null || password.length() < 6) {
        redirectAttributes.addFlashAttribute("errorMessage", "La contraseña debe tener al menos 6 caracteres");
        return "redirect:/register?error";
      }
      if (!password.equals(confirmPassword)) {
        redirectAttributes.addFlashAttribute("errorMessage", "Las contraseñas no coinciden");
        return "redirect:/register?error";
      }

      // Verificar si el email ya existe
      if (clienteService.buscarPorEmail(email.trim()).isPresent()) {
        redirectAttributes.addFlashAttribute("errorMessage", "El correo electrónico ya está registrado");
        return "redirect:/register?error";
      }

      // Generar username desde el email (antes de @)
      String baseUsername = email.trim().toLowerCase();
      int atIdx = baseUsername.indexOf('@');
      baseUsername = (atIdx > 0) ? baseUsername.substring(0, atIdx) : baseUsername;
      // Normalizar caracteres no alfanuméricos
      baseUsername = baseUsername.replaceAll("[^a-z0-9._-]", "_");

      // Evitar colisiones de username si ya existe en clientes
      String candidate = baseUsername;
      int suffix = 1;
      while (clienteService.buscarPorUsername(candidate).isPresent()) {
        candidate = baseUsername + suffix;
        suffix++;
      }

      // Construir entidad Cliente (registros públicos)
      Cliente nuevo = new Cliente();
      nuevo.setUsername(candidate);
      nuevo.setEmail(email.trim());
      nuevo.setPassword(password);
      nuevo.setNombreCompleto((firstName.trim() + " " + lastName.trim()).trim());
      
      // Campos opcionales
      if (telefono != null && !telefono.isBlank()) {
        nuevo.setTelefono(telefono.trim());
      }
      if (direccion != null && !direccion.isBlank()) {
        nuevo.setDireccion(direccion.trim());
      }

      clienteService.registrar(nuevo);

      // Guardar en sesión el username para realizar un login diferido cuando el usuario pulse "Volver al inicio"
      HttpSession session = request.getSession(true);
      session.setAttribute("postRegisterUsername", candidate);

      redirectAttributes.addFlashAttribute("successMessage", "¡Cuenta creada exitosamente! Tu nombre de usuario es: " + candidate);
      return "redirect:/register?success";
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      return "redirect:/register?error";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la cuenta. Por favor intenta nuevamente");
      return "redirect:/register?error";
    }
  }
  
  @GetMapping("/post-register")
  public String postRegisterLogin(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      String username = (String) session.getAttribute("postRegisterUsername");
      if (username != null) {
        try {
          var userDetails = usuarioService.loadUserByUsername(username);
          UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(auth);
          // Persistir el SecurityContext en la sesión para que Spring Security lo reconozca en siguientes requests
          session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
          // limpiar atributo de sesión
          session.removeAttribute("postRegisterUsername");
        } catch (Exception e) {
          // ignore and continue to redirect to home
        }
      }
    }
    return "redirect:/";
  }
}
