package com.revature.revpay.service.impl;

import com.revature.revpay.dao.LoanDAO;
import com.revature.revpay.dao.impl.LoanDAOImpl;
import com.revature.revpay.model.Loan;
import com.revature.revpay.service.LoanService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class LoanServiceImpl implements LoanService {
    private final LoanDAO loanDAO;
    
    public LoanServiceImpl() {
        this.loanDAO = new LoanDAOImpl();
    }
    
    @Override
    public Loan applyForLoan(Long businessUserId, BigDecimal loanAmount, BigDecimal interestRate, 
                            Integer termMonths, String purpose) {
        if (loanAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Loan amount must be greater than zero");
        }
        
        if (termMonths <= 0) {
            throw new IllegalArgumentException("Term months must be greater than zero");
        }
        
        Loan loan = new Loan(businessUserId, loanAmount, purpose, termMonths);
        loan.setInterestRate(interestRate);
        
        // Calculate total amount with interest
        BigDecimal interestAmount = loanAmount.multiply(interestRate)
            .multiply(BigDecimal.valueOf(termMonths))
            .divide(BigDecimal.valueOf(1200), 2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = loanAmount.add(interestAmount);
        loan.setTotalAmount(totalAmount);
        loan.setRemainingAmount(totalAmount);
        
        return loanDAO.save(loan);
    }
    
    @Override
    public List<Loan> getLoansByUser(Long businessUserId) {
        return loanDAO.findByBusinessUserId(businessUserId);
    }
    
    @Override
    public boolean repayLoan(Long loanId, Long businessUserId, BigDecimal amount) {
        Optional<Loan> loanOpt = loanDAO.findById(loanId);
        if (loanOpt.isEmpty()) {
            return false;
        }
        
        Loan loan = loanOpt.get();
        
        if (!loan.getBusinessUserId().equals(businessUserId)) {
            throw new IllegalArgumentException("You are not authorized to repay this loan");
        }
        
        if (loan.getStatus() != Loan.LoanStatus.DISBURSED) {
            throw new IllegalStateException("Loan is not in disbursed state");
        }
        
        if (amount.compareTo(loan.getRemainingAmount()) > 0) {
            throw new IllegalArgumentException("Repayment amount exceeds remaining balance");
        }
        
        BigDecimal newBalance = loan.getRemainingAmount().subtract(amount);
        loan.setRemainingAmount(newBalance);
        
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(Loan.LoanStatus.REPAID);
        }
        
        return loanDAO.update(loan);
    }
}
