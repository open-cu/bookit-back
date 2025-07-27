package com.opencu.bookit.application.service.nofication;


import com.opencu.bookit.application.port.out.nofication.NotificationPort;
import com.opencu.bookit.application.port.out.nofication.NotificationQueuePort;
import com.opencu.bookit.application.port.out.user.UserPreferencesPort;
import com.opencu.bookit.domain.model.event.EventNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationQueuePort notificationQueuePort;
    private final NotificationPort notificationPort;
    private final UserPreferencesPort userPreferencesPort;

    @Value("${booking.zone-id}")
    ZoneId zoneId;
    
    public void scheduleEventNotification(EventNotification notification, LocalDateTime date) {
        if (userPreferencesPort.isSubscribedToNotifications(notification.getUserId())) {
            long delayInMillis = date.isAfter(LocalDateTime.now())
                ? java.time.Duration.between(LocalDateTime.now(), date).toMillis()
                : 0;
            notificationQueuePort.scheduleNotification(notification, delayInMillis);
        }
    }
    
    public void sendEventNotificationNow(EventNotification notification) {
        if (userPreferencesPort.isSubscribedToNotifications(notification.getUserId())) {
            notificationPort.sendNotification(notification);
        }
    }
    
    public void cancelNotification(UUID notificationId) {
        notificationQueuePort.cancelNotification(notificationId);
    }
    
    public void unsubscribeFromNotifications(UUID userId) {
        userPreferencesPort.setNotificationPreference(userId, false);
    }
    
    public void subscribeToNotifications(UUID userId) {
        userPreferencesPort.setNotificationPreference(userId, true);
    }

    public boolean isSubscribedToNotifications(UUID userId) {
        return userPreferencesPort.isSubscribedToNotifications(userId);
    }
}