package com.proyectoweb.Juledtoys.controladores;

import com.proyectoweb.Juledtoys.dto.ChatbotRequest;
import com.proyectoweb.Juledtoys.dto.ChatbotResponse;
import com.proyectoweb.Juledtoys.servicios.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el chatbot con Gemini AI
 */
@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * Endpoint para recibir mensajes del usuario y devolver respuestas de IA
     */
    @PostMapping("/mensaje")
    public ResponseEntity<ChatbotResponse> procesarMensaje(@RequestBody ChatbotRequest request) {
        try {
            // Validar que el mensaje no esté vacío
            if (request.getMensaje() == null || request.getMensaje().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ChatbotResponse.error("El mensaje no puede estar vacío"));
            }

            // Log del mensaje recibido
            System.out.println("💬 [Chatbot] Mensaje recibido: " + request.getMensaje());

            // Generar respuesta con Gemini
            String respuesta = chatbotService.generarRespuesta(request.getMensaje());

            // Generar o usar ID de conversación existente
            String conversacionId = request.getConversacionId() != null 
                    ? request.getConversacionId() 
                    : chatbotService.generarConversacionId();

            // Log de la respuesta generada
            System.out.println("🤖 [Chatbot] Respuesta generada: " + respuesta);

            ChatbotResponse response = new ChatbotResponse(respuesta, conversacionId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ [Chatbot] Error al procesar mensaje: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(ChatbotResponse.error("Error al procesar tu mensaje"));
        }
    }

    /**
     * Endpoint de prueba para verificar que el chatbot está funcionando
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("✅ Chatbot API funcionando correctamente");
    }
}
