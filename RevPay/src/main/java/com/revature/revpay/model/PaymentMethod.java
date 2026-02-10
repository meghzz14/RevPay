package com.revature.revpay.model;

import java.time.LocalDateTime;

public class PaymentMethod {
    private Long paymentMethodId;
    private Long userId;
    private PaymentType type;
    private String encryptedCardNumber;
    private String cardHolderName;
    private String expiryMonth;
    private String expiryYear;
    private String encryptedCvv;
    private String bankName;
    private String accountNumber;
    private String routingNumber;
    private boolean isDefault;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    public enum PaymentType {
        CREDIT_CARD, DEBIT_CARD, BANK_ACCOUNT
    }
    
    // Constructors
    public PaymentMethod() {}
    
    public PaymentMethod(Long userId, PaymentType type, String cardHolderName) {
        this.userId = userId;
        this.type = type;
        this.cardHolderName = cardHolderName;
        this.isActive = true;
        this.isDefault = false;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(Long paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public PaymentType getType() { return type; }
    public void setType(PaymentType type) { this.type = type; }
    
    public String getEncryptedCardNumber() { return encryptedCardNumber; }
    public void setEncryptedCardNumber(String encryptedCardNumber) { this.encryptedCardNumber = encryptedCardNumber; }
    
    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    
    public String getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }
    
    public String getExpiryYear() { return expiryYear; }
    public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }
    
    public String getEncryptedCvv() { return encryptedCvv; }
    public void setEncryptedCvv(String encryptedCvv) { this.encryptedCvv = encryptedCvv; }
    
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getRoutingNumber() { return routingNumber; }
    public void setRoutingNumber(String routingNumber) { this.routingNumber = routingNumber; }
    
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getMaskedCardNumber() {
        if (encryptedCardNumber != null && encryptedCardNumber.length() >= 4) {
            return "**** **** **** " + encryptedCardNumber.substring(encryptedCardNumber.length() - 4);
        }
        return "****";
    }
}