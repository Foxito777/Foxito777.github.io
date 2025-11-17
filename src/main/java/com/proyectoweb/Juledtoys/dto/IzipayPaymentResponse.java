package com.proyectoweb.Juledtoys.dto;

public class IzipayPaymentResponse {
    private boolean success;
    private String formToken;
    private String publicKey;
    private String jsUrl;
    private String message;
    private String orderId;
    
    public IzipayPaymentResponse() {
    }
    
    public IzipayPaymentResponse(boolean success, String formToken, String publicKey, String jsUrl, String orderId) {
        this.success = success;
        this.formToken = formToken;
        this.publicKey = publicKey;
        this.jsUrl = jsUrl;
        this.orderId = orderId;
    }
    
    public static IzipayPaymentResponse error(String message) {
        IzipayPaymentResponse response = new IzipayPaymentResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
    
    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getFormToken() {
        return formToken;
    }
    
    public void setFormToken(String formToken) {
        this.formToken = formToken;
    }
    
    public String getPublicKey() {
        return publicKey;
    }
    
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    
    public String getJsUrl() {
        return jsUrl;
    }
    
    public void setJsUrl(String jsUrl) {
        this.jsUrl = jsUrl;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
