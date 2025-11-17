package com.proyectoweb.Juledtoys.dto;

/**
 * DTO para enviar respuestas del chatbot al frontend
 */
public class ChatbotResponse {
    
    private String respuesta;
    private String conversacionId;
    private boolean exito;
    private String error;
    
    public ChatbotResponse() {
    }
    
    public ChatbotResponse(String respuesta, String conversacionId) {
        this.respuesta = respuesta;
        this.conversacionId = conversacionId;
        this.exito = true;
    }
    
    public static ChatbotResponse error(String mensajeError) {
        ChatbotResponse response = new ChatbotResponse();
        response.exito = false;
        response.error = mensajeError;
        response.respuesta = "Lo siento, ocurrió un error al procesar tu mensaje. Por favor, intenta nuevamente.";
        return response;
    }
    
    public String getRespuesta() {
        return respuesta;
    }
    
    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
    
    public String getConversacionId() {
        return conversacionId;
    }
    
    public void setConversacionId(String conversacionId) {
        this.conversacionId = conversacionId;
    }
    
    public boolean isExito() {
        return exito;
    }
    
    public void setExito(boolean exito) {
        this.exito = exito;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
