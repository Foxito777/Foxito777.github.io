package com.proyectoweb.Juledtoys.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.math.BigDecimal;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:juledtoy@juledtoys.com}")
    private String fromEmail;

    /**
     * Envía un correo con el comprobante de pago Yape
     */
    public void enviarComprobanteYape(String numeroPedido, String codigoOperacion, 
                                      String numeroTelefono, MultipartFile comprobante,
                                      String emailCliente, BigDecimal totalPedido) throws MessagingException, IOException {
        
        // Verificar si el servicio de email está configurado
        if (mailSender == null) {
            System.out.println("⚠️ [Email] Servicio de email no configurado. Comprobante no enviado.");
            System.out.println("   Configure spring.mail.username y spring.mail.password en application-hosting.properties");
            logComprobanteEnConsola(numeroPedido, codigoOperacion, numeroTelefono, emailCliente, totalPedido);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo("edison@juledtoys.com");
            helper.setSubject("Comprobante de Pago Yape - Pedido " + numeroPedido);

            String htmlContent = construirHtmlComprobante(numeroPedido, codigoOperacion, numeroTelefono, emailCliente, totalPedido);
            helper.setText(htmlContent, true);

            // Adjuntar comprobante
            if (comprobante != null && !comprobante.isEmpty()) {
                ByteArrayResource resource = new ByteArrayResource(comprobante.getBytes());
                helper.addAttachment(comprobante.getOriginalFilename(), resource);
            }

            mailSender.send(message);
            System.out.println("📧 [Email] Comprobante Yape enviado exitosamente a edison@juledtoys.com");
            System.out.println("   📦 Pedido: " + numeroPedido);
        } catch (Exception e) {
            System.err.println("❌ [Email] Error al enviar correo: " + e.getMessage());
            System.err.println("   Verifica la configuración de spring.mail en application-hosting.properties");
            logComprobanteEnConsola(numeroPedido, codigoOperacion, numeroTelefono, emailCliente, totalPedido);
            // No lanzar excepción para que el proceso continúe
        }
    }

    private void logComprobanteEnConsola(String numeroPedido, String codigoOperacion, 
                                         String numeroTelefono, String emailCliente, BigDecimal totalPedido) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📱 COMPROBANTE YAPE RECIBIDO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📦 Pedido:           " + numeroPedido);
        System.out.println("🔢 Código Operación: " + codigoOperacion);
        System.out.println("📱 Teléfono:         " + numeroTelefono);
        System.out.println("📧 Email Cliente:    " + emailCliente);
        System.out.println("💰 Total:            S/ " + (totalPedido != null ? totalPedido : "0.00"));
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private String construirHtmlComprobante(String numeroPedido, String codigoOperacion, 
                                           String numeroTelefono, String emailCliente, BigDecimal totalPedido) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f9f9f9;
                    }
                    .header {
                        background-color: #4CAF50;
                        color: white;
                        padding: 20px;
                        text-align: center;
                        border-radius: 5px 5px 0 0;
                    }
                    .content {
                        background-color: white;
                        padding: 30px;
                        border-radius: 0 0 5px 5px;
                    }
                    .info-row {
                        padding: 10px 0;
                        border-bottom: 1px solid #eee;
                    }
                    .label {
                        font-weight: bold;
                        color: #555;
                    }
                    .value {
                        color: #333;
                        margin-left: 10px;
                    }
                    .total {
                        font-size: 24px;
                        color: #4CAF50;
                        font-weight: bold;
                        margin: 20px 0;
                        text-align: center;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>💳 Comprobante de Pago Yape</h1>
                    </div>
                    <div class="content">
                        <h2>Nuevo pago recibido</h2>
                        <p>Se ha recibido un comprobante de pago vía Yape:</p>
                        
                        <div class="info-row">
                            <span class="label">📦 Número de Pedido:</span>
                            <span class="value">""" + numeroPedido + """
</span>
                        </div>
                        
                        <div class="info-row">
                            <span class="label">🔢 Código de Operación:</span>
                            <span class="value">""" + codigoOperacion + """
</span>
                        </div>
                        
                        <div class="info-row">
                            <span class="label">📱 Teléfono:</span>
                            <span class="value">""" + numeroTelefono + """
</span>
                        </div>
                        
                        <div class="info-row">
                            <span class="label">📧 Email del Cliente:</span>
                            <span class="value">""" + emailCliente + """
</span>
                        </div>
                        
                        <div class="total">
                            Total: S/ """ + (totalPedido != null ? String.format("%.2f", totalPedido.doubleValue()) : "0.00") + """
                        </div>
                        
                        <p style="margin-top: 30px; padding-top: 20px; border-top: 2px solid #4CAF50;">
                            <strong>Nota:</strong> El comprobante de pago está adjunto a este correo.
                            Por favor, verifica la información y procede con la confirmación del pedido.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}
