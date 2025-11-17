package com.proyectoweb.Juledtoys.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestEmailController {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:no-configurado}")
    private String fromEmail;

    @Value("${spring.mail.host:no-configurado}")
    private String host;

    @Value("${spring.mail.port:0}")
    private int port;

    @GetMapping("/email")
    public String testEmail() {
        StringBuilder result = new StringBuilder();
        result.append("🔧 PRUEBA DE CONFIGURACIÓN DE EMAIL\n\n");
        result.append("📧 Host: ").append(host).append("\n");
        result.append("🔌 Puerto: ").append(port).append("\n");
        result.append("👤 Usuario: ").append(fromEmail).append("\n\n");

        if (mailSender == null) {
            result.append("❌ JavaMailSender no está configurado\n");
            return result.toString();
        }

        result.append("✅ JavaMailSender está configurado\n\n");

        try {
            result.append("📤 Intentando enviar email de prueba...\n");
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo("edison@juledtoys.com");
            message.setSubject("🧪 Prueba de Email - Juledtoys");
            message.setText("Este es un email de prueba desde el sistema Juledtoys.\n\n" +
                          "Si recibes este mensaje, la configuración de email está funcionando correctamente.");

            mailSender.send(message);
            
            result.append("✅ Email enviado exitosamente a edison@juledtoys.com\n");
            result.append("📬 Revisa tu bandeja de entrada\n");
            
        } catch (Exception e) {
            result.append("❌ ERROR al enviar email:\n");
            result.append("   Tipo: ").append(e.getClass().getSimpleName()).append("\n");
            result.append("   Mensaje: ").append(e.getMessage()).append("\n");
            
            if (e.getCause() != null) {
                result.append("   Causa: ").append(e.getCause().getMessage()).append("\n");
            }
            
            result.append("\n💡 SOLUCIONES POSIBLES:\n");
            result.append("1. Verifica que las credenciales sean correctas\n");
            result.append("2. Verifica que el puerto 465 esté abierto\n");
            result.append("3. Intenta con puerto 587 (STARTTLS)\n");
            result.append("4. Verifica en el panel de hosting si el email está activo\n");
        }

        return result.toString();
    }

    @GetMapping("/email/info")
    public String emailInfo() {
        return "Host: " + host + "\n" +
               "Puerto: " + port + "\n" +
               "Usuario: " + fromEmail + "\n" +
               "MailSender: " + (mailSender != null ? "Configurado" : "No configurado");
    }
}
