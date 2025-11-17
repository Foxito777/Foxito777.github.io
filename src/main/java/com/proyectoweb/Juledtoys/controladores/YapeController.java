package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.dto.YapeComprobanteDTO;
import com.proyectoweb.Juledtoys.entidades.Cliente;
import com.proyectoweb.Juledtoys.entidades.Pedido;
import com.proyectoweb.Juledtoys.servicios.ClienteService;
import com.proyectoweb.Juledtoys.servicios.EmailService;
import com.proyectoweb.Juledtoys.servicios.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/yape")
public class YapeController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmailService emailService;

    /**
     * Página de pago con Yape: muestra QR y formulario para subir comprobante
     */
    @GetMapping("/pago")
    public String paginaPagoYape(Model model) {
        // Los datos del pedido vienen de redirectAttributes
        if (!model.containsAttribute("numeroPedido")) {
            return "redirect:/checkout";
        }
        
        model.addAttribute("comprobanteDTO", new YapeComprobanteDTO());
        return "pago-yape";
    }

    /**
     * Página de confirmación después de enviar el comprobante
     */
    @GetMapping("/pago-pendiente")
    public String pagoPendiente(Model model) {
        // Los datos vienen de redirectAttributes
        if (!model.containsAttribute("numeroPedido")) {
            return "redirect:/checkout";
        }
        return "pago-pendiente";
    }

    /**
     * Procesa el comprobante de pago Yape y envía email
     */
    @PostMapping("/enviar-comprobante")
    public String enviarComprobante(@ModelAttribute YapeComprobanteDTO dto,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Validaciones básicas
            if (dto.getNumeroPedido() == null || dto.getNumeroPedido().isBlank()) {
                redirectAttributes.addFlashAttribute("mensaje", "Número de pedido inválido");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/checkout";
            }

            if (dto.getCodigoOperacion() == null || dto.getCodigoOperacion().isBlank()) {
                redirectAttributes.addFlashAttribute("mensaje", "Debes ingresar el código de operación");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                redirectAttributes.addFlashAttribute("numeroPedido", dto.getNumeroPedido());
                return "redirect:/yape/pago";
            }

            if (dto.getNumeroTelefono() == null || dto.getNumeroTelefono().isBlank()) {
                redirectAttributes.addFlashAttribute("mensaje", "Debes ingresar el número de teléfono");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                redirectAttributes.addFlashAttribute("numeroPedido", dto.getNumeroPedido());
                return "redirect:/yape/pago";
            }

            if (dto.getComprobante() == null || dto.getComprobante().isEmpty()) {
                redirectAttributes.addFlashAttribute("mensaje", "Debes subir el comprobante de pago");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                redirectAttributes.addFlashAttribute("numeroPedido", dto.getNumeroPedido());
                return "redirect:/yape/pago";
            }

            // Validar tamaño del archivo (máximo 5MB)
            if (dto.getComprobante().getSize() > 5 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("mensaje", "El archivo es muy grande (máximo 5MB)");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                redirectAttributes.addFlashAttribute("numeroPedido", dto.getNumeroPedido());
                return "redirect:/yape/pago";
            }

            // Validar tipo de archivo
            String contentType = dto.getComprobante().getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
                redirectAttributes.addFlashAttribute("mensaje", "Solo se permiten imágenes o PDF");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                redirectAttributes.addFlashAttribute("numeroPedido", dto.getNumeroPedido());
                return "redirect:/yape/pago";
            }

            // Buscar pedido
            Optional<Pedido> pedidoOpt = pedidoService.buscarPorNumero(dto.getNumeroPedido());
            if (pedidoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("mensaje", "Pedido no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/checkout";
            }

            Pedido pedido = pedidoOpt.get();
            
            // Obtener cliente autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Cliente cliente = clienteService.buscarPorUsername(auth.getName()).orElse(null);
            
            if (cliente == null || !pedido.getCliente().getId().equals(cliente.getId())) {
                redirectAttributes.addFlashAttribute("mensaje", "No tienes permiso para este pedido");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/checkout";
            }

            // Enviar email con comprobante (no lanza excepción si falla)
            emailService.enviarComprobanteYape(
                dto.getNumeroPedido(),
                dto.getCodigoOperacion(),
                dto.getNumeroTelefono(),
                dto.getComprobante(),
                cliente.getEmail(),
                pedido.getTotal()
            );

            System.out.println("✅ [Yape] Comprobante procesado para pedido: " + dto.getNumeroPedido());
            System.out.println("   📱 Teléfono: " + dto.getNumeroTelefono());
            System.out.println("   🔢 Código: " + dto.getCodigoOperacion());
            System.out.println("   📧 Email enviado a: juledtoy@juledtoys.com");

            // Redirigir a página de pago pendiente
            redirectAttributes.addFlashAttribute("numeroPedido", pedido.getNumeroPedido());
            redirectAttributes.addFlashAttribute("totalPedido", pedido.getTotal());
            redirectAttributes.addFlashAttribute("emailCliente", cliente.getEmail());
            return "redirect:/yape/pago-pendiente";

        } catch (Exception e) {
            System.err.println("❌ [Yape] Error al procesar comprobante: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensaje", "Error al enviar comprobante: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/checkout";
        }
    }
}
