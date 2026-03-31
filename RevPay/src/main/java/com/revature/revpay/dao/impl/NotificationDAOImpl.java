package com.revature.revpay.dao.impl;

import com.revature.revpay.dao.NotificationDAO;
import com.revature.revpay.model.Notification;
import com.revature.revpay.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificationDAOImpl implements NotificationDAO {
    
    @Override
    public Notification save(Notification notification) {
        String sql = "INSERT INTO notifications (notification_id, user_id, type, title, message, is_read) " +
                    "VALUES (notifications_seq.NEXTVAL, ?, ?, ?, ?, ?)";
        String getIdSql = "SELECT notifications_seq.CURRVAL FROM DUAL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, notification.getUserId());
            stmt.setString(2, notification.getType().name());
            stmt.setString(3, notification.getTitle());
            stmt.setString(4, notification.getMessage());
            stmt.setInt(5, notification.isRead() ? 1 : 0);
            
            stmt.executeUpdate();
            
            try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    notification.setNotificationId(rs.getLong(1));
                }
            }
            
            return notification;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving notification", e);
        }
    }
    
    @Override
    public Optional<Notification> findById(Long notificationId) {
        String sql = "SELECT * FROM notifications WHERE notification_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, notificationId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToNotification(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding notification", e);
        }
    }
    
    @Override
    public List<Notification> findByUserId(Long userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
            return notifications;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding notifications", e);
        }
    }
    
    @Override
    public List<Notification> findUnreadByUserId(Long userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = 0 ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
            return notifications;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding unread notifications", e);
        }
    }
    
    @Override
    public boolean markAsRead(Long notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE notification_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error marking notification as read", e);
        }
    }
    
    @Override
    public boolean markAllAsRead(Long userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error marking all notifications as read", e);
        }
    }
    
    @Override
    public int countUnread(Long userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting unread notifications", e);
        }
    }
    
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getLong("notification_id"));
        notification.setUserId(rs.getLong("user_id"));
        notification.setType(Notification.NotificationType.valueOf(rs.getString("type")));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setRead(rs.getInt("is_read") == 1);
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            notification.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return notification;
    }
}
