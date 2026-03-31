package com.revature.revpay.dao;

import com.revature.revpay.model.Loan;
import java.util.List;
import java.util.Optional;

public interface LoanDAO {
    Loan save(Loan loan);
    Optional<Loan> findById(Long loanId);
    List<Loan> findByBusinessUserId(Long businessUserId);
    boolean update(Loan loan);
}
