package com.revature.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Long transactionId;
    private Long fromUserId;
    private Long toUserId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String description;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String referenceNumber;
    
    public enum TransactionType {
        SEND_MONEY, REQUEST_MONEY, ADD_MONEY, WITHDRAW_MONEY, 
        PAYMENT_RECEIVED, INVOICE_PAYMENT, LOAN_DISBURSEMENT, LOAN_REPAYMENT
    }
    
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED, DECLINED
    }
    
    // Constructors
    public Transaction() {}
    
    public Transaction(Long fromUserId, Long toUserId, BigDecimal amount, 
                      TransactionType type, String description) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.status = TransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.referenceNumber = generateReferenceNumber();
    }
    
    private String generateReferenceNumber() {
        return "TXN" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    
    public Long getFromUserId() { return fromUserId; }
    public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }
    
    public Long getToUserId() { return toUserId; }
    public void setToUserId(Long toUserId) { this.toUserId = toUserId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
}