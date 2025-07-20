package com.opencu.bookit.application.service.nofication;


import com.opencu.bookit.application.port.out.nofication.NotificationPort;
import com.opencu.bookit.application.port.out.nofication.NotificationQueuePort;
import com.opencu.bookit.application.port.out.user.UserPreferencesPort;
import com.opencu.bookit.domain.model.event.EventNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationQueuePort notificationQueuePort;
    private final NotificationPort notificationPort;
    private final UserPreferencesPort userPreferencesPort;
    
    public void scheduleEventNotification(EventNotification notification, long minutesBeforeEvent) {
        if (userPreferencesPort.isSubscribedToNotifications(notification.getUserId())) {
            long delayInMillis = minutesBeforeEvent * 60 * 1000;
            notificationQueuePort.scheduleNotification(notification, delayInMillis);
        }
    }
    
    public void sendNotificationNow(EventNotification notification) {
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