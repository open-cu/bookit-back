package com.opencu.bookit.application.port.out.nofication;

import java.util.UUID;

public interface DeleteDelayedNotificationPort {
    void cancelNotification(UUID userId, UUID eventId);
}
