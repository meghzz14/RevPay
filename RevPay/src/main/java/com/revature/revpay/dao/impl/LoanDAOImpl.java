package com.revature.revpay.dao.impl;

import com.revature.revpay.dao.LoanDAO;
import com.revature.revpay.model.Loan;
import com.revature.revpay.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanDAOImpl implements LoanDAO {
    
    @Override
    public Loan save(Loan loan) {
        String sql = "INSERT INTO loans (loan_id, business_user_id, loan_amount, purpose, status, " +
                    "interest_rate, term_months, outstanding_balance, applied_at) " +
                    "VALUES (loans_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        String getIdSql = "SELECT loans_seq.CURRVAL FROM DUAL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, loan.getBusinessUserId());
            stmt.setBigDecimal(2, loan.getLoanAmount());
            stmt.setString(3, loan.getPurpose());
            stmt.setString(4, loan.getStatus().name());
            stmt.setBigDecimal(5, loan.getInterestRate());
            stmt.setInt(6, loan.getTermMonths());
            stmt.setBigDecimal(7, loan.getRemainingAmount());
            
            stmt.executeUpdate();
            
            try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    loan.setLoanId(rs.getLong(1));
                }
            }
            
            return loan;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving loan", e);
        }
    }
    
    @Override
    public Optional<Loan> findById(Long loanId) {
        String sql = "SELECT * FROM loans WHERE loan_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, loanId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToLoan(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding loan", e);
        }
    }
    
    @Override
    public List<Loan> findByBusinessUserId(Long businessUserId) {
        String sql = "SELECT * FROM loans WHERE business_user_id = ? ORDER BY applied_at DESC";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, businessUserId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
            return loans;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding loans", e);
        }
    }
    
    @Override
    public boolean update(Loan loan) {
        String sql = "UPDATE loans SET status = ?, interest_rate = ?, monthly_payment = ?, " +
                    "outstanding_balance = ?, approved_at = ?, disbursed_at = ? WHERE loan_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, loan.getStatus().name());
            stmt.setBigDecimal(2, loan.getInterestRate());
            stmt.setBigDecimal(3, loan.getMonthlyPayment());
            stmt.setBigDecimal(4, loan.getRemainingAmount());
            stmt.setTimestamp(5, loan.getApprovedAt() != null ? Timestamp.valueOf(loan.getApprovedAt()) : null);
            stmt.setTimestamp(6, loan.getDisbursedAt() != null ? Timestamp.valueOf(loan.getDisbursedAt()) : null);
            stmt.setLong(7, loan.getLoanId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating loan", e);
        }
    }
    
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(rs.getLong("loan_id"));
        loan.setBusinessUserId(rs.getLong("business_user_id"));
        loan.setLoanAmount(rs.getBigDecimal("loan_amount"));
        loan.setPurpose(rs.getString("purpose"));
        loan.setStatus(Loan.LoanStatus.valueOf(rs.getString("status")));
        loan.setInterestRate(rs.getBigDecimal("interest_rate"));
        loan.setTermMonths(rs.getInt("term_months"));
        loan.setMonthlyPayment(rs.getBigDecimal("monthly_payment"));
        loan.setRemainingAmount(rs.getBigDecimal("outstanding_balance"));
        
        // Calculate total amount from loan amount and remaining amount
        if (loan.getInterestRate() != null && loan.getLoanAmount() != null && loan.getTermMonths() != null) {
            java.math.BigDecimal interestAmount = loan.getLoanAmount()
                .multiply(loan.getInterestRate())
                .multiply(java.math.BigDecimal.valueOf(loan.getTermMonths()))
                .divide(java.math.BigDecimal.valueOf(1200), 2, java.math.RoundingMode.HALF_UP);
            loan.setTotalAmount(loan.getLoanAmount().add(interestAmount));
        }
        
        Timestamp appliedAt = rs.getTimestamp("applied_at");
        if (appliedAt != null) {
            loan.setAppliedAt(appliedAt.toLocalDateTime());
            loan.setCreatedAt(appliedAt.toLocalDateTime());
        }
        
        Timestamp approvedAt = rs.getTimestamp("approved_at");
        if (approvedAt != null) {
            loan.setApprovedAt(approvedAt.toLocalDateTime());
        }
        
        Timestamp disbursedAt = rs.getTimestamp("disbursed_at");
        if (disbursedAt != null) {
            loan.setDisbursedAt(disbursedAt.toLocalDateTime());
        }
        
        return loan;
    }
}
