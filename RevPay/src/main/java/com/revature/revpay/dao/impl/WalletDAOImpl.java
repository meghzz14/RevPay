package com.revature.revpay.dao.impl;

import com.revature.revpay.dao.WalletDAO;
import com.revature.revpay.model.Wallet;
import com.revature.revpay.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

public class WalletDAOImpl implements WalletDAO {
    
    @Override
    public Optional<Wallet> findByUserId(Long userId) {
        String sql = "SELECT * FROM wallets WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Wallet wallet = new Wallet();
                wallet.setWalletId(rs.getLong("wallet_id"));
                wallet.setUserId(rs.getLong("user_id"));
                wallet.setBalance(rs.getBigDecimal("balance"));
                
                Timestamp lastUpdated = rs.getTimestamp("last_updated");
                if (lastUpdated != null) {
                    wallet.setLastUpdated(lastUpdated.toLocalDateTime());
                }
                
                return Optional.of(wallet);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding wallet", e);
        }
    }
    
    @Override
    public boolean updateBalance(Long userId, BigDecimal newBalance) {
        String sql = "UPDATE wallets SET balance = ?, last_updated = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, newBalance);
            stmt.setLong(2, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating wallet balance", e);
        }
    }
}
