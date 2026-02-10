package com.revature.revpay.service;

import com.revature.revpay.dao.NotificationDAO;
import com.revature.revpay.dao.impl.NotificationDAOImpl;
import com.revature.revpay.model.Notification;

import java.util.List;

public class NotificationService {
    private final NotificationDAO notificationDAO;
    
    public NotificationService() {
        this.notificationDAO = new NotificationDAOImpl();
    }
    
    public Notification createNotification(Long userId, Notification.NotificationType type, 
                                          String title, String message) {
        Notification notification = new Notification(userId, type, title, message);
        return notificationDAO.save(notification);
    }
    
    public List<Notification> getAllNotifications(Long userId) {
        return notificationDAO.findByUserId(userId);
    }
    
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationDAO.findUnreadByUserId(userId);
    }
    
    public int getUnreadCount(Long userId) {
        return notificationDAO.countUnread(userId);
    }
    
    public boolean markAsRead(Long notificationId) {
        return notificationDAO.markAsRead(notificationId);
    }
    
    public boolean markAllAsRead(Long userId) {
        return notificationDAO.markAllAsRead(userId);
    }
}
