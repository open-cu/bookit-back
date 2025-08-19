package com.opencu.bookit.application.service.nofication;


import com.opencu.bookit.application.port.out.event.LoadEventPort;
import com.opencu.bookit.application.port.out.nofication.DeleteDelayedNotificationPort;
import com.opencu.bookit.application.port.out.nofication.NotificationPort;
import com.opencu.bookit.application.port.out.nofication.NotificationQueuePort;
import com.opencu.bookit.application.port.out.nofication.SaveDelayedEventNotificationPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.port.out.user.UserPreferencesPort;
import com.opencu.bookit.domain.model.event.EventNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationQueuePort notificationQueuePort;
    private final NotificationPort notificationPort;
    private final UserPreferencesPort userPreferencesPort;
    private final DeleteDelayedNotificationPort deleteDelayedNotificationPort;
    private final LoadUserPort loadUserPort;
    private final LoadEventPort loadEventPort;
    private final SaveDelayedEventNotificationPort saveDelayedEventNotificationPort;

    @Value("${booking.zone-id}")
    ZoneId zoneId;

    @Value("${messaging.enabled}")
    private boolean messagingEnabled;
    
    public void scheduleEventNotification(EventNotification notification, LocalDateTime notificationTime) {
        if (!messagingEnabled) {
            return;
        }
        if (userPreferencesPort.isSubscribedToNotifications(notification.getUserId())) {
            long delayInMillis = notificationTime.isAfter(LocalDateTime.now())
                ? java.time.Duration.between(LocalDateTime.now(), notificationTime).toMillis()
                : 0;
            notificationQueuePort.scheduleNotification(notification, delayInMillis);
            saveDelayedEventNotificationPort.save(notification);
        }
        else {
            log.info("User with ID {} is not subscribed to notifications, skipping scheduling", notification.getUserId());
        }
    }
    
    public void sendEventNotificationNow(EventNotification notification) {
        if (!messagingEnabled) {
            return;
        }

        if (loadEventPort.existsById(notification.getEventId())) {
            log.debug("Event with ID {} exists, proceeding with notification", notification.getEventId());
        }
        else {
            log.warn("Event with ID {} does not exist, skipping notification", notification.getEventId());
            return;
        }
        if (loadUserPort.existsById(notification.getUserId())) {
            log.debug("User with ID {} exists, proceeding with notification", notification.getUserId());
        }
        else {
            log.warn("User with ID {} does not exist, skipping notification", notification.getUserId());
            return;
        }

        if (userPreferencesPort.isSubscribedToNotifications(notification.getUserId())) {
            notificationPort.sendNotification(notification);
        }
    }
    
    public void cancelNotification(UUID userId, UUID eventId) {
        deleteDelayedNotificationPort.cancelNotification(userId, eventId);
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