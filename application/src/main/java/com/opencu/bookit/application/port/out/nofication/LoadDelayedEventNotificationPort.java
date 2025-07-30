package com.opencu.bookit.application.port.out.nofication;

import com.opencu.bookit.domain.model.event.EventNotification;

import java.util.Optional;
import java.util.UUID;

public interface LoadDelayedEventNotificationPort {
    Optional<EventNotification> loadById(UUID id);
}
