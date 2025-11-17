package com.proyectoweb.Juledtoys.servicios;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio para integrar Google Gemini AI como chatbot
 */
@Service
public class ChatbotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    // Contexto del sistema: información sobre Juledtoys
    private static final String SYSTEM_CONTEXT = """
            Eres un asistente virtual de Juledtoys, una tienda online de juguetes en Perú.
            
            INFORMACIÓN DE LA TIENDA:
            - Nombre: Juledtoys
            - Productos: Juguetes educativos, peluches, juegos de mesa, figuras de acción, muñecas, vehículos de juguete, etc.
            - Público objetivo: Niños de 0-12 años
            - Moneda: Soles peruanos (S/)
            
            MÉTODOS DE PAGO:
            1. Tarjeta de crédito/débito (Visa, Mastercard, American Express) - A través de IZIPAY
            2. Yape - Pago con código QR, el cliente sube el comprobante y en 24-48 horas se confirma
            
            ENVÍOS:
            - Envío a todo el Perú
            - Tiempo de entrega: 3-7 días hábiles
            - Costo de envío: Calculado según destino y peso
            - Envío gratis en compras mayores a S/150
            
            DEVOLUCIONES Y GARANTÍAS:
            - 30 días para devoluciones
            - Los productos deben estar en su empaque original
            - Garantía del fabricante en productos defectuosos
            
            HORARIOS DE ATENCIÓN:
            - Lunes a Viernes: 9:00 AM - 6:00 PM
            - Sábados: 9:00 AM - 1:00 PM
            - Email de contacto: edison@juledtoys.com
            
            INSTRUCCIONES:
            - Sé amable, profesional y útil
            - Responde en español
            - Si no sabes algo, ofrece contactar al equipo de soporte
            - Recomienda productos cuando sea apropiado
            - Ayuda con dudas sobre pedidos, pagos y envíos
            - Sé breve pero informativo
            """;

    public ChatbotService() {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Genera una respuesta del chatbot usando Gemini AI
     */
    public String generarRespuesta(String mensajeUsuario) {
        try {
            logger.info("💬 [Chatbot] Mensaje recibido: {}", mensajeUsuario);
            
            // Construir el prompt completo con contexto
            String promptCompleto = SYSTEM_CONTEXT + "\n\nUsuario: " + mensajeUsuario + "\n\nAsistente:";

            // Preparar el JSON de la petición
            Map<String, Object> requestBody = new HashMap<>();
            
            Map<String, Object> content = new HashMap<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", promptCompleto);
            content.put("parts", List.of(part));
            
            requestBody.put("contents", List.of(content));
            
            // Configuración de generación
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 500);
            generationConfig.put("topP", 0.95);
            requestBody.put("generationConfig", generationConfig);

            // Configuración de seguridad (permitir todo para chatbot de tienda)
            List<Map<String, String>> safetySettings = List.of(
                Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_NONE"),
                Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_NONE"),
                Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_NONE"),
                Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_NONE")
            );
            requestBody.put("safetySettings", safetySettings);

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            logger.info("📤 [Chatbot] Request body preparado");

            // Hacer la petición HTTP
            String urlConKey = apiUrl + "?key=" + apiKey;
            logger.info("🌐 [Chatbot] URL: {}", apiUrl);
            
            Request request = new Request.Builder()
                    .url(urlConKey)
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                logger.info("📡 [Chatbot] Response code: {}", response.code());
                
                if (!response.isSuccessful()) {
                    String errorBody = response.body().string();
                    logger.error("❌ Error en Gemini API: {}", response.code());
                    logger.error("Cuerpo: {}", errorBody);
                    return "Lo siento, estoy teniendo problemas técnicos. Por favor, contacta a edison@juledtoys.com";
                }

                String responseBody = response.body().string();
                logger.info("✅ [Chatbot] Respuesta recibida exitosamente");
                
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                
                // Extraer la respuesta del JSON
                JsonNode candidates = jsonResponse.get("candidates");
                if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                    JsonNode firstCandidate = candidates.get(0);
                    JsonNode contentNode = firstCandidate.get("content");
                    if (contentNode != null) {
                        JsonNode parts = contentNode.get("parts");
                        if (parts != null && parts.isArray() && parts.size() > 0) {
                            JsonNode firstPart = parts.get(0);
                            JsonNode text = firstPart.get("text");
                            if (text != null) {
                                String respuesta = text.asText().trim();
                                logger.info("🤖 [Chatbot] Respuesta generada: {}", respuesta.substring(0, Math.min(50, respuesta.length())) + "...");
                                return respuesta;
                            }
                        }
                    }
                }
                
                logger.warn("⚠️ [Chatbot] No se pudo extraer texto de la respuesta");
                return "Lo siento, no pude generar una respuesta. ¿Puedes reformular tu pregunta?";
            }

        } catch (IOException e) {
            logger.error("❌ Error al conectar con Gemini: {}", e.getMessage());
            e.printStackTrace();
            return "Estoy experimentando problemas de conexión. Por favor, intenta nuevamente en unos segundos.";
        } catch (Exception e) {
            logger.error("❌ Error inesperado en ChatbotService: {}", e.getMessage());
            e.printStackTrace();
            return "Ha ocurrido un error inesperado. Por favor, contacta a nuestro equipo de soporte.";
        }
    }

    /**
     * Genera un ID único para la conversación
     */
    public String generarConversacionId() {
        return UUID.randomUUID().toString();
    }
}
