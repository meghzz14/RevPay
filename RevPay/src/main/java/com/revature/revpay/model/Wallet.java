package com.revature.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Wallet {
    private Long walletId;
    private Long userId;
    private BigDecimal balance;
    private LocalDateTime lastUpdated;
    
    // Constructors
    public Wallet() {}
    
    public Wallet(Long userId) {
        this.userId = userId;
        this.balance = BigDecimal.ZERO;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { 
        this.balance = balance;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public void addAmount(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.lastUpdated = LocalDateTime.now();
    }
    
    public boolean deductAmount(BigDecimal amount) {
        if (this.balance.compareTo(amount) >= 0) {
            this.balance = this.balance.subtract(amount);
            this.lastUpdated = LocalDateTime.now();
            return true;
        }
        return false;
    }
}