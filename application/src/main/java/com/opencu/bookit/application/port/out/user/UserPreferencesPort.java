package com.opencu.bookit.application.port.out.user;

import java.util.UUID;

public interface UserPreferencesPort {
    boolean isSubscribedToNotifications(UUID userId);
    void setNotificationPreference(UUID userId, boolean subscribed);
}