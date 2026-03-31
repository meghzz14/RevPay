package com.revature.revpay.dao.impl;

import com.revature.revpay.dao.UserDAO;
import com.revature.revpay.model.User;
import com.revature.revpay.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    
    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (user_id, full_name, email, phone_number, password_hash, transaction_pin, " +
                    "security_question1, security_answer1, security_question2, security_answer2, " +
                    "account_type, status, business_name, business_type, tax_id, business_address) " +
                    "VALUES (users_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        String getIdSql = "SELECT users_seq.CURRVAL FROM DUAL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getTransactionPin());
            stmt.setString(6, user.getSecurityQuestion1());
            stmt.setString(7, user.getSecurityAnswer1());
            stmt.setString(8, user.getSecurityQuestion2());
            stmt.setString(9, user.getSecurityAnswer2());
            stmt.setString(10, user.getAccountType().name());
            stmt.setString(11, user.getStatus().name());
            stmt.setString(12, user.getBusinessName());
            stmt.setString(13, user.getBusinessType());
            stmt.setString(14, user.getTaxId());
            stmt.setString(15, user.getBusinessAddress());
            
            stmt.executeUpdate();
            
            try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    user.setUserId(rs.getLong(1));
                }
            }
            
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user", e);
        }
    }
    
    @Override
    public Optional<User> findById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by ID", e);
        }
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email", e);
        }
    }
    
    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM users WHERE phone_number = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by phone", e);
        }
    }
    
    @Override
    public List<User> findByFullNameContaining(String name) {
        String sql = "SELECT * FROM users WHERE UPPER(full_name) LIKE UPPER(?)";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding users by name", e);
        }
    }
    
    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone_number = ?, password_hash = ?, " +
                    "transaction_pin = ?, security_question1 = ?, security_answer1 = ?, " +
                    "security_question2 = ?, security_answer2 = ?, status = ?, last_login = ?, " +
                    "failed_login_attempts = ?, is_locked = ?, business_name = ?, business_type = ?, " +
                    "tax_id = ?, business_address = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getTransactionPin());
            stmt.setString(6, user.getSecurityQuestion1());
            stmt.setString(7, user.getSecurityAnswer1());
            stmt.setString(8, user.getSecurityQuestion2());
            stmt.setString(9, user.getSecurityAnswer2());
            stmt.setString(10, user.getStatus().name());
            stmt.setTimestamp(11, user.getLastLogin() != null ? Timestamp.valueOf(user.getLastLogin()) : null);
            stmt.setInt(12, user.getFailedLoginAttempts());
            stmt.setBoolean(13, user.isLocked());
            stmt.setString(14, user.getBusinessName());
            stmt.setString(15, user.getBusinessType());
            stmt.setString(16, user.getTaxId());
            stmt.setString(17, user.getBusinessAddress());
            stmt.setLong(18, user.getUserId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }
    
    @Override
    public boolean delete(Long userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking email existence", e);
        }
    }
    
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        String sql = "SELECT COUNT(*) FROM users WHERE phone_number = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking phone existence", e);
        }
    }
    
    @Override
    public void incrementFailedLoginAttempts(Long userId) {
        String sql = "UPDATE users SET failed_login_attempts = failed_login_attempts + 1 WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error incrementing failed login attempts", e);
        }
    }
    
    @Override
    public void resetFailedLoginAttempts(Long userId) {
        String sql = "UPDATE users SET failed_login_attempts = 0 WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error resetting failed login attempts", e);
        }
    }
    
    @Override
    public void lockAccount(Long userId) {
        String sql = "UPDATE users SET is_locked = 1 WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error locking account", e);
        }
    }
    
    @Override
    public void unlockAccount(Long userId) {
        String sql = "UPDATE users SET is_locked = 0, failed_login_attempts = 0 WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error unlocking account", e);
        }
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setTransactionPin(rs.getString("transaction_pin"));
        user.setSecurityQuestion1(rs.getString("security_question1"));
        user.setSecurityAnswer1(rs.getString("security_answer1"));
        user.setSecurityQuestion2(rs.getString("security_question2"));
        user.setSecurityAnswer2(rs.getString("security_answer2"));
        user.setAccountType(User.AccountType.valueOf(rs.getString("account_type")));
        user.setStatus(User.AccountStatus.valueOf(rs.getString("status")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        user.setLocked(rs.getBoolean("is_locked"));
        user.setBusinessName(rs.getString("business_name"));
        user.setBusinessType(rs.getString("business_type"));
        user.setTaxId(rs.getString("tax_id"));
        user.setBusinessAddress(rs.getString("business_address"));
        
        return user;
    }
}