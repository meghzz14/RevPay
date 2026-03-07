package com.revpay.model;

import com.revpay.model.enums.NotificationType;
import com.revpay.model.enums.YesNoStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "NOTIFICATION_PREFERENCES")
@Data
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notif_pref_seq")
    @SequenceGenerator(name = "notif_pref_seq", sequenceName = "GEN_NOTIF_PREF_ID", allocationSize = 1)
    private Long preferenceId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "ENABLED")
    private YesNoStatus enabled;
}