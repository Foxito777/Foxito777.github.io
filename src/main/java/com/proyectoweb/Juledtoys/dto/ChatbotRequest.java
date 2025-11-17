package com.proyectoweb.Juledtoys.dto;

/**
 * DTO para recibir mensajes del chatbot desde el frontend
 */
public class ChatbotRequest {
    
    private String mensaje;
    private String conversacionId; // Opcional: para mantener contexto de conversación
    
    public ChatbotRequest() {
    }
    
    public ChatbotRequest(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public ChatbotRequest(String mensaje, String conversacionId) {
        this.mensaje = mensaje;
        this.conversacionId = conversacionId;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public String getConversacionId() {
        return conversacionId;
    }
    
    public void setConversacionId(String conversacionId) {
        this.conversacionId = conversacionId;
    }
}
