package com.proyectoweb.Juledtoys.servicios;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectoweb.Juledtoys.config.IzipayConfig;
import com.proyectoweb.Juledtoys.dto.IzipayPaymentRequest;
import com.proyectoweb.Juledtoys.dto.IzipayPaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class IzipayService {
    
    @Autowired
    private IzipayConfig izipayConfig;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Crea un formToken para procesar el pago
     */
    public IzipayPaymentResponse createPaymentForm(IzipayPaymentRequest request) {
        try {
            System.out.println("🔄 [Izipay] Creando formToken para orden: " + request.getOrderId());
            
            // Convertir monto a centavos (Izipay requiere el monto en la unidad mínima)
            BigDecimal amountInCents = request.getAmount().multiply(new BigDecimal("100"));
            
            // Construir el request JSON
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("amount", amountInCents.intValue());
            paymentData.put("currency", request.getCurrency());
            paymentData.put("orderId", request.getOrderId());
            
            Map<String, Object> customer = new HashMap<>();
            customer.put("email", request.getCustomerEmail());
            paymentData.put("customer", customer);
            
            String jsonBody = objectMapper.writeValueAsString(paymentData);
            System.out.println("📤 [Izipay] Request: " + jsonBody);
            
            // Configurar headers con autenticación Basic
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Para Izipay REST API, el usuario es el Shop ID y la contraseña incluye el prefijo
            String username = izipayConfig.getShop().getId();
            String password = izipayConfig.getCurrentPassword();
            
            // Debug: mostrar credenciales
            System.out.println("🔑 [Izipay] Username (Shop ID): " + username);
            System.out.println("🔑 [Izipay] Password: " + password);
            System.out.println("🔑 [Izipay] Public Key (para frontend): " + izipayConfig.getCurrentPublicKey());
            
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            headers.set("Authorization", "Basic " + encodedAuth);
            
            // Hacer POST request
            String url = izipayConfig.getApi().getUrl() + "/api-payment/V4/Charge/CreatePayment";
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            System.out.println("📥 [Izipay] Response status: " + response.getStatusCode());
            System.out.println("📥 [Izipay] Response body: " + response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                
                if (jsonResponse.has("answer") && jsonResponse.path("answer").has("formToken")) {
                    String formToken = jsonResponse.path("answer").path("formToken").asText();
                    System.out.println("✅ [Izipay] FormToken creado exitosamente");
                    
                    return new IzipayPaymentResponse(
                        true,
                        formToken,
                        izipayConfig.getCurrentPublicKey(),
                        izipayConfig.getJs().getUrl(),
                        request.getOrderId()
                    );
                } else {
                    // Log toda la respuesta para debug
                    System.err.println("❌ [Izipay] Respuesta JSON completa: " + jsonResponse.toString());
                    
                    String errorMessage = "Error desconocido";
                    if (jsonResponse.has("status") && jsonResponse.get("status").asText().equals("ERROR")) {
                        if (jsonResponse.has("answer")) {
                            JsonNode answer = jsonResponse.get("answer");
                            if (answer.has("detailedErrorMessage") && !answer.get("detailedErrorMessage").isNull()) {
                                errorMessage = answer.get("detailedErrorMessage").asText();
                            } else if (answer.has("errorMessage") && !answer.get("errorMessage").isNull()) {
                                errorMessage = answer.get("errorMessage").asText();
                            } else if (answer.has("errorCode") && !answer.get("errorCode").isNull()) {
                                errorMessage = "Error code: " + answer.get("errorCode").asText();
                            }
                        }
                    } else if (!jsonResponse.has("answer")) {
                        errorMessage = "No se recibió 'answer' en la respuesta";
                    } else if (!jsonResponse.path("answer").has("formToken")) {
                        errorMessage = "No se recibió 'formToken' en la respuesta";
                    }
                    
                    System.err.println("❌ [Izipay] Error: " + errorMessage);
                    return IzipayPaymentResponse.error(errorMessage);
                }
            } else {
                String errorMsg = "Error HTTP: " + response.getStatusCode();
                System.err.println("❌ [Izipay] " + errorMsg);
                return IzipayPaymentResponse.error(errorMsg);
            }
            
        } catch (Exception e) {
            System.err.println("❌ [Izipay] Excepción: " + e.getMessage());
            e.printStackTrace();
            return IzipayPaymentResponse.error("Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    /**
     * Verifica la firma HMAC-SHA-256 del webhook
     */
    public boolean verifySignature(String krHash, String krHashAlgorithm, String krAnswer) {
        try {
            if (!"sha256_hmac".equals(krHashAlgorithm)) {
                System.err.println("❌ [Izipay] Algoritmo de hash no soportado: " + krHashAlgorithm);
                return false;
            }
            
            // Obtener la clave HMAC según el entorno
            String hmacKey = izipayConfig.getCurrentHmacKey();
            
            // Calcular el hash
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hmacKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            
            byte[] hash = mac.doFinal(krAnswer.getBytes(StandardCharsets.UTF_8));
            String calculatedHash = bytesToHex(hash);
            
            boolean isValid = calculatedHash.equalsIgnoreCase(krHash);
            
            if (isValid) {
                System.out.println("✅ [Izipay] Firma verificada correctamente");
            } else {
                System.err.println("❌ [Izipay] Firma inválida");
                System.err.println("   Recibida: " + krHash);
                System.err.println("   Calculada: " + calculatedHash);
            }
            
            return isValid;
            
        } catch (Exception e) {
            System.err.println("❌ [Izipay] Error verificando firma: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Convierte bytes a representación hexadecimal
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
