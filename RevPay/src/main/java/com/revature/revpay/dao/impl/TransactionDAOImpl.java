package com.revature.revpay.dao.impl;

import com.revature.revpay.dao.TransactionDAO;
import com.revature.revpay.model.Transaction;
import com.revature.revpay.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAOImpl implements TransactionDAO {
    
    @Override
    public Transaction save(Transaction transaction) {
        String sql = "INSERT INTO transactions (transaction_id, from_user_id, to_user_id, amount, type, status, " +
                    "description, note, reference_number) VALUES (transactions_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";
        String getIdSql = "SELECT transactions_seq.CURRVAL FROM DUAL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, transaction.getFromUserId());
            stmt.setObject(2, transaction.getToUserId());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setString(4, transaction.getType().name());
            stmt.setString(5, transaction.getStatus().name());
            stmt.setString(6, transaction.getDescription());
            stmt.setString(7, transaction.getNote());
            stmt.setString(8, transaction.getReferenceNumber());
            
            stmt.executeUpdate();
            
            try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    transaction.setTransactionId(rs.getLong(1));
                }
            }
            
            return transaction;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving transaction", e);
        }
    }
    
    @Override
    public Optional<Transaction> findById(Long transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToTransaction(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding transaction", e);
        }
    }
    
    @Override
    public List<Transaction> findByUserId(Long userId) {
        String sql = "SELECT * FROM transactions WHERE from_user_id = ? OR to_user_id = ? ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding transactions", e);
        }
    }
    
    @Override
    public List<Transaction> findByFromUserId(Long fromUserId) {
        String sql = "SELECT * FROM transactions WHERE from_user_id = ? ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, fromUserId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding transactions", e);
        }
    }
    
    @Override
    public List<Transaction> findByToUserId(Long toUserId) {
        String sql = "SELECT * FROM transactions WHERE to_user_id = ? ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, toUserId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding transactions", e);
        }
    }
    
    @Override
    public boolean update(Transaction transaction) {
        String sql = "UPDATE transactions SET status = ?, completed_at = ? WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transaction.getStatus().name());
            stmt.setTimestamp(2, transaction.getCompletedAt() != null ? 
                             Timestamp.valueOf(transaction.getCompletedAt()) : null);
            stmt.setLong(3, transaction.getTransactionId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating transaction", e);
        }
    }
    
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getLong("transaction_id"));
        transaction.setFromUserId(rs.getObject("from_user_id", Long.class));
        transaction.setToUserId(rs.getObject("to_user_id", Long.class));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setType(Transaction.TransactionType.valueOf(rs.getString("type")));
        transaction.setStatus(Transaction.TransactionStatus.valueOf(rs.getString("status")));
        transaction.setDescription(rs.getString("description"));
        transaction.setNote(rs.getString("note"));
        transaction.setReferenceNumber(rs.getString("reference_number"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            transaction.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp completedAt = rs.getTimestamp("completed_at");
        if (completedAt != null) {
            transaction.setCompletedAt(completedAt.toLocalDateTime());
        }
        
        return transaction;
    }
}
