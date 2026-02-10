package com.revature.revpay.dao;

import com.revature.revpay.model.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationDAO {
    Notification save(Notification notification);
    Optional<Notification> findById(Long notificationId);
    List<Notification> findByUserId(Long userId);
    List<Notification> findUnreadByUserId(Long userId);
    boolean markAsRead(Long notificationId);
    boolean markAllAsRead(Long userId);
    int countUnread(Long userId);
}
