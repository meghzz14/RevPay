package com.revpay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revpay.model.NotificationPreference;
import com.revpay.model.User;
import com.revpay.model.enums.NotificationType;

public interface NotificationPreferenceRepository
        extends JpaRepository<NotificationPreference, Long> {

    Optional<NotificationPreference> findByUserAndType(User user, NotificationType type);

    List<NotificationPreference> findByUser(User user);
}