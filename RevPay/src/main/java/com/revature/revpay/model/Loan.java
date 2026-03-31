package com.revature.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Loan {
    private Long loanId;
    private Long businessUserId;
    private BigDecimal loanAmount;
    private String purpose;
    private LoanStatus status;
    private BigDecimal interestRate;
    private Integer termMonths;
    private BigDecimal monthlyPayment;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime disbursedAt;
    private LocalDateTime createdAt;
    
    public enum LoanStatus {
        APPLIED, UNDER_REVIEW, APPROVED, REJECTED, DISBURSED, REPAID
    }
    
    public Loan() {}
    
    public Loan(Long businessUserId, BigDecimal loanAmount, String purpose, Integer termMonths) {
        this.businessUserId = businessUserId;
        this.loanAmount = loanAmount;
        this.purpose = purpose;
        this.termMonths = termMonths;
        this.status = LoanStatus.APPLIED;
        this.appliedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.remainingAmount = loanAmount;
    }
    
    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }
    
    public Long getBusinessUserId() { return businessUserId; }
    public void setBusinessUserId(Long businessUserId) { this.businessUserId = businessUserId; }
    
    public BigDecimal getLoanAmount() { return loanAmount; }
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }
    
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
    
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    
    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
    
    public BigDecimal getMonthlyPayment() { return monthlyPayment; }
    public void setMonthlyPayment(BigDecimal monthlyPayment) { this.monthlyPayment = monthlyPayment; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public LocalDateTime getDisbursedAt() { return disbursedAt; }
    public void setDisbursedAt(LocalDateTime disbursedAt) { this.disbursedAt = disbursedAt; }
}
