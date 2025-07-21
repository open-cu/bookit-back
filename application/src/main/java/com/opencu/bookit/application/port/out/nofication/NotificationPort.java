package com.opencu.bookit.application.port.out.nofication;

import com.opencu.bookit.domain.model.event.EventNotification;

public interface NotificationPort {
    void sendNotification(EventNotification notification);
}