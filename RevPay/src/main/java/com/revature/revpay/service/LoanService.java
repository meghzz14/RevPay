package com.revature.revpay.service;

import com.revature.revpay.model.Loan;
import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    Loan applyForLoan(Long businessUserId, BigDecimal loanAmount, BigDecimal interestRate, 
                     Integer termMonths, String purpose);
    List<Loan> getLoansByUser(Long businessUserId);
    boolean repayLoan(Long loanId, Long businessUserId, BigDecimal amount);
}
