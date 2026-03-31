package com.revature.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MoneyRequest {
    private Long requestId;
    private Long requesterId;
    private Long requestedFromId;
    private BigDecimal amount;
    private String description;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    
    public enum RequestStatus {
        PENDING, ACCEPTED, DECLINED, CANCELLED
    }
    
    public MoneyRequest() {}
    
    public MoneyRequest(Long requesterId, Long requestedFromId, BigDecimal amount, String description) {
        this.requesterId = requesterId;
        this.requestedFromId = requestedFromId;
        this.amount = amount;
        this.description = description;
        this.status = RequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
    
    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }
    
    public Long getRequestedFromId() { return requestedFromId; }
    public void setRequestedFromId(Long requestedFromId) { this.requestedFromId = requestedFromId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
}
