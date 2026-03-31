package com.revature.revpay.dao.impl;

import com.revature.revpay.dao.PaymentMethodDAO;
import com.revature.revpay.model.PaymentMethod;
import com.revature.revpay.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentMethodDAOImpl implements PaymentMethodDAO {
    
    @Override
    public PaymentMethod save(PaymentMethod paymentMethod) {
        String sql = "INSERT INTO payment_methods (payment_method_id, user_id, type, encrypted_card_number, " +
                    "card_holder_name, expiry_month, expiry_year, encrypted_cvv, bank_name, account_number, " +
                    "routing_number, is_default, is_active) " +
                    "VALUES (payment_methods_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String getIdSql = "SELECT payment_methods_seq.CURRVAL FROM DUAL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, paymentMethod.getUserId());
            stmt.setString(2, paymentMethod.getType().name());
            stmt.setString(3, paymentMethod.getEncryptedCardNumber());
            stmt.setString(4, paymentMethod.getCardHolderName());
            stmt.setString(5, paymentMethod.getExpiryMonth());
            stmt.setString(6, paymentMethod.getExpiryYear());
            stmt.setString(7, paymentMethod.getEncryptedCvv());
            stmt.setString(8, paymentMethod.getBankName());
            stmt.setString(9, paymentMethod.getAccountNumber());
            stmt.setString(10, paymentMethod.getRoutingNumber());
            stmt.setInt(11, paymentMethod.isDefault() ? 1 : 0);
            stmt.setInt(12, paymentMethod.isActive() ? 1 : 0);
            
            stmt.executeUpdate();
            
            try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    paymentMethod.setPaymentMethodId(rs.getLong(1));
                }
            }
            
            return paymentMethod;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving payment method", e);
        }
    }
    
    @Override
    public Optional<PaymentMethod> findById(Long paymentMethodId) {
        String sql = "SELECT * FROM payment_methods WHERE payment_method_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, paymentMethodId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPaymentMethod(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payment method", e);
        }
    }
    
    @Override
    public List<PaymentMethod> findByUserId(Long userId) {
        String sql = "SELECT * FROM payment_methods WHERE user_id = ? AND is_active = 1 ORDER BY is_default DESC, created_at DESC";
        List<PaymentMethod> methods = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                methods.add(mapResultSetToPaymentMethod(rs));
            }
            return methods;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payment methods", e);
        }
    }
    
    @Override
    public Optional<PaymentMethod> findDefaultByUserId(Long userId) {
        String sql = "SELECT * FROM payment_methods WHERE user_id = ? AND is_default = 1 AND is_active = 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPaymentMethod(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding default payment method", e);
        }
    }
    
    @Override
    public boolean update(PaymentMethod paymentMethod) {
        String sql = "UPDATE payment_methods SET is_default = ?, is_active = ? WHERE payment_method_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, paymentMethod.isDefault() ? 1 : 0);
            stmt.setInt(2, paymentMethod.isActive() ? 1 : 0);
            stmt.setLong(3, paymentMethod.getPaymentMethodId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating payment method", e);
        }
    }
    
    @Override
    public boolean delete(Long paymentMethodId) {
        String sql = "UPDATE payment_methods SET is_active = 0 WHERE payment_method_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, paymentMethodId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting payment method", e);
        }
    }
    
    @Override
    public boolean setDefaultPaymentMethod(Long userId, Long paymentMethodId) {
        String clearDefaultSql = "UPDATE payment_methods SET is_default = 0 WHERE user_id = ?";
        String setDefaultSql = "UPDATE payment_methods SET is_default = 1 WHERE payment_method_id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement clearStmt = conn.prepareStatement(clearDefaultSql);
                 PreparedStatement setStmt = conn.prepareStatement(setDefaultSql)) {
                
                clearStmt.setLong(1, userId);
                clearStmt.executeUpdate();
                
                setStmt.setLong(1, paymentMethodId);
                setStmt.setLong(2, userId);
                int rows = setStmt.executeUpdate();
                
                conn.commit();
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error setting default payment method", e);
        }
    }
    
    private PaymentMethod mapResultSetToPaymentMethod(ResultSet rs) throws SQLException {
        PaymentMethod method = new PaymentMethod();
        method.setPaymentMethodId(rs.getLong("payment_method_id"));
        method.setUserId(rs.getLong("user_id"));
        method.setType(PaymentMethod.PaymentType.valueOf(rs.getString("type")));
        method.setEncryptedCardNumber(rs.getString("encrypted_card_number"));
        method.setCardHolderName(rs.getString("card_holder_name"));
        method.setExpiryMonth(rs.getString("expiry_month"));
        method.setExpiryYear(rs.getString("expiry_year"));
        method.setEncryptedCvv(rs.getString("encrypted_cvv"));
        method.setBankName(rs.getString("bank_name"));
        method.setAccountNumber(rs.getString("account_number"));
        method.setRoutingNumber(rs.getString("routing_number"));
        method.setDefault(rs.getInt("is_default") == 1);
        method.setActive(rs.getInt("is_active") == 1);
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            method.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return method;
    }
}
