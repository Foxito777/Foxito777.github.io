package com.proyectoweb.Juledtoys.dto;

import java.math.BigDecimal;

public class IzipayPaymentRequest {
    private Long pedidoId;
    private BigDecimal amount;
    private String currency;
    private String orderId;
    private String customerEmail;
    
    public IzipayPaymentRequest() {
    }
    
    public IzipayPaymentRequest(Long pedidoId, BigDecimal amount, String currency, String orderId, String customerEmail) {
        this.pedidoId = pedidoId;
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
        this.customerEmail = customerEmail;
    }
    
    // Getters y Setters
    public Long getPedidoId() {
        return pedidoId;
    }
    
    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
