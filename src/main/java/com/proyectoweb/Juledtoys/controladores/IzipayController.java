package com.proyectoweb.Juledtoys.controladores;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectoweb.Juledtoys.dto.IzipayPaymentRequest;
import com.proyectoweb.Juledtoys.dto.IzipayPaymentResponse;
import com.proyectoweb.Juledtoys.entidades.EstadoPedido;
import com.proyectoweb.Juledtoys.entidades.Pedido;
import com.proyectoweb.Juledtoys.servicios.IzipayService;
import com.proyectoweb.Juledtoys.servicios.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.Optional;

@Controller
@RequestMapping("/izipay")
public class IzipayController {
    
    @Autowired
    private IzipayService izipayService;
    
    @Autowired
    private PedidoService pedidoService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Crea el formulario de pago de Izipay
     */
    @PostMapping("/create-payment")
    @ResponseBody
    public ResponseEntity<IzipayPaymentResponse> createPayment(@RequestBody IzipayPaymentRequest request) {
        try {
            // Validar que el pedido existe
            Optional<Pedido> pedidoOpt = pedidoService.buscarPorId(request.getPedidoId());
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(IzipayPaymentResponse.error("Pedido no encontrado"));
            }
            
            Pedido pedido = pedidoOpt.get();
            
            // Crear el formulario de pago
            IzipayPaymentResponse response = izipayService.createPaymentForm(request);
            
            if (response.isSuccess()) {
                // Actualizar el pedido a PENDIENTE_PAGO
                pedido.setEstado(EstadoPedido.PENDIENTE_PAGO);
                pedidoService.guardar(pedido);
                
                System.out.println("💳 [Izipay] FormToken creado para pedido: " + pedido.getNumeroPedido());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [Izipay] Error creando pago: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(IzipayPaymentResponse.error("Error interno al procesar el pago"));
        }
    }
    
    /**
     * Webhook/IPN para recibir notificaciones de pago
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestParam("kr-hash") String krHash,
            @RequestParam("kr-hash-algorithm") String krHashAlgorithm,
            @RequestParam("kr-answer") String krAnswer) {
        
        try {
            System.out.println("🔔 [Izipay] Webhook recibido");
            
            // Verificar la firma
            if (!izipayService.verifySignature(krHash, krHashAlgorithm, krAnswer)) {
                System.err.println("❌ [Izipay] Firma inválida");
                return ResponseEntity.status(403).body("Invalid signature");
            }
            
            // Decodificar la respuesta
            String decodedAnswer = new String(Base64.getDecoder().decode(krAnswer));
            JsonNode answer = objectMapper.readTree(decodedAnswer);
            
            // Obtener información del pago
            String orderId = answer.path("orderDetails").path("orderId").asText();
            String transactionStatus = answer.path("transactions").get(0).path("status").asText();
            
            System.out.println("📦 [Izipay] Pedido: " + orderId + ", Estado: " + transactionStatus);
            
            // Buscar el pedido
            Optional<Pedido> pedidoOpt = pedidoService.buscarPorNumero(orderId);
            if (pedidoOpt.isEmpty()) {
                System.err.println("❌ [Izipay] Pedido no encontrado: " + orderId);
                return ResponseEntity.ok("Order not found");
            }
            
            Pedido pedido = pedidoOpt.get();
            
            // Actualizar estado según la transacción
            if ("PAID".equals(transactionStatus)) {
                pedido.setEstado(EstadoPedido.CONFIRMADO);
                pedidoService.guardar(pedido);
                System.out.println("✅ [Izipay] Pago confirmado para pedido: " + orderId);
            } else if ("REFUSED".equals(transactionStatus) || "ABANDONED".equals(transactionStatus)) {
                pedido.setEstado(EstadoPedido.CANCELADO);
                pedidoService.guardar(pedido);
                System.out.println("❌ [Izipay] Pago rechazado para pedido: " + orderId);
            }
            
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            System.err.println("❌ [Izipay] Error procesando webhook: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error");
        }
    }
    
    /**
     * Página de retorno después del pago
     */
    @GetMapping("/return")
    public String paymentReturn(
            @RequestParam(value = "kr-hash", required = false) String krHash,
            @RequestParam(value = "kr-hash-algorithm", required = false) String krHashAlgorithm,
            @RequestParam(value = "kr-answer", required = false) String krAnswer,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (krAnswer == null) {
                redirectAttributes.addFlashAttribute("mensaje", "No se recibió respuesta del pago");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/mis-compras";
            }
            
            // Verificar firma
            if (!izipayService.verifySignature(krHash, krHashAlgorithm, krAnswer)) {
                redirectAttributes.addFlashAttribute("mensaje", "Respuesta de pago inválida");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/mis-compras";
            }
            
            // Decodificar respuesta
            String decodedAnswer = new String(Base64.getDecoder().decode(krAnswer));
            JsonNode answer = objectMapper.readTree(decodedAnswer);
            
            String orderId = answer.path("orderDetails").path("orderId").asText();
            String transactionStatus = answer.path("transactions").get(0).path("status").asText();
            String orderStatus = answer.path("orderStatus").asText();
            
            System.out.println("🔄 [Izipay] Return - Pedido: " + orderId + ", Estado transacción: " + transactionStatus);
            
            // Buscar pedido
            Optional<Pedido> pedidoOpt = pedidoService.buscarPorNumero(orderId);
            
            if ("PAID".equals(transactionStatus)) {
                // Actualizar el estado del pedido a CONFIRMADO
                if (pedidoOpt.isPresent()) {
                    Pedido pedido = pedidoOpt.get();
                    pedido.setEstado(EstadoPedido.CONFIRMADO);
                    pedidoService.guardar(pedido);
                    System.out.println("✅ [Izipay] Pedido confirmado: " + orderId);
                    
                    model.addAttribute("totalPedido", pedido.getTotal());
                    model.addAttribute("emailCliente", pedido.getCliente().getEmail());
                }
                model.addAttribute("success", true);
                model.addAttribute("numeroPedido", orderId);
                return "pago-exitoso";
            } else {
                // Actualizar a CANCELADO si el pago fue rechazado
                if (pedidoOpt.isPresent()) {
                    Pedido pedido = pedidoOpt.get();
                    pedido.setEstado(EstadoPedido.CANCELADO);
                    pedidoService.guardar(pedido);
                    System.out.println("❌ [Izipay] Pedido cancelado: " + orderId);
                }
                model.addAttribute("success", false);
                model.addAttribute("mensaje", "El pago no pudo ser procesado");
                return "pago-fallido";
            }
            
        } catch (Exception e) {
            System.err.println("❌ [Izipay] Error en página de retorno: " + e.getMessage());
            redirectAttributes.addFlashAttribute("mensaje", "Error al procesar la respuesta del pago");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/mis-compras";
        }
    }
}
