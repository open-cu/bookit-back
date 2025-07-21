package com.opencu.bookit.application.port.out.nofication;

import com.opencu.bookit.domain.model.event.EventNotification;

import java.util.UUID;

public interface NotificationQueuePort {
    void scheduleNotification(EventNotification notification, long delayInMilliseconds);
    void cancelNotification(UUID notificationId);
}