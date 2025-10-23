package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.servicios.CarritoServiceJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ContactoController {

    @Autowired
    private CarritoServiceJPA carritoService;

    @GetMapping("/contacto")
    public String contacto(Model model) {
        // Agregar el conteo del carrito al modelo
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        
        // Agregar información de contacto
        model.addAttribute("telefono", "935-867-730");
        model.addAttribute("email", "info@juledtoys.com");
        model.addAttribute("direccion", "Av. El Ejército Nº105, Ayacucho, Perú");
        model.addAttribute("horarios", "Lunes a Sábado: 9:00 AM - 8:00 PM");
        
        // Agregar ubicaciones de tiendas
        model.addAttribute("tiendas", obtenerTiendas());
        
        return "contacto";
    }

    @PostMapping("/contacto/enviar")
    public String enviarMensaje(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            @RequestParam(required = false) String newsletter,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Aquí podrías integrar con un servicio de email real
            // Por ahora simulamos el envío
            
            // Simular procesamiento
            Thread.sleep(1000);
            
            // Log del mensaje (en un caso real, guardarías en BD o enviarías email)
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            System.out.println("=== NUEVO MENSAJE DE CONTACTO ===");
            System.out.println("Fecha: " + timestamp);
            System.out.println("Nombre: " + nombre);
            System.out.println("Email: " + email);
            System.out.println("Teléfono: " + telefono);
            System.out.println("Asunto: " + asunto);
            System.out.println("Mensaje: " + mensaje);
            System.out.println("Newsletter: " + (newsletter != null ? "Sí" : "No"));
            System.out.println("==================================");
            
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "¡Gracias " + nombre + "! Tu mensaje ha sido enviado correctamente. Te responderemos pronto.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Ocurrió un error al enviar tu mensaje. Por favor, intenta nuevamente.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        
        return "redirect:/contacto";
    }

    @PostMapping("/contacto/newsletter")
    public String suscribirNewsletter(
            @RequestParam String emailNewsletter,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Simular suscripción al newsletter
            System.out.println("Nueva suscripción al newsletter: " + emailNewsletter);
            
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "¡Te has suscrito exitosamente a nuestro newsletter!");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Error al suscribirse al newsletter. Intenta nuevamente.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        
        return "redirect:/contacto";
    }

    private java.util.List<java.util.Map<String, String>> obtenerTiendas() {
        // Simular datos de tiendas
        java.util.List<java.util.Map<String, String>> tiendas = new java.util.ArrayList<>();
        
        java.util.Map<String, String> tienda1 = new java.util.HashMap<>();
        tienda1.put("nombre", "Tienda Principal Ayacucho");
        tienda1.put("direccion", "Av. El Ejército Nº105");
        tienda1.put("telefono", "935-867-730");
        tienda1.put("horarios", "L-S: 9:00 AM - 8:00 PM");
        tienda1.put("latitud", "-13.1631");
        tienda1.put("longitud", "-74.2236");
        tiendas.add(tienda1);
        
        java.util.Map<String, String> tienda2 = new java.util.HashMap<>();
        tienda2.put("nombre", "Sucursal Centro Comercial");
        tienda2.put("direccion", "Mall Ayacucho, Local 215");
        tienda2.put("telefono", "967-306-892");
        tienda2.put("horarios", "L-D: 10:00 AM - 10:00 PM");
        tienda2.put("latitud", "-13.1581");
        tienda2.put("longitud", "-74.2186");
        tiendas.add(tienda2);
        
        return tiendas;
    }
}