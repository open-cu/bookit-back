package com.opencu.bookit.config;

import com.opencu.bookit.adapter.out.email.spi.EmailProvider;
import com.opencu.bookit.application.feature.AppFeatures;
import com.opencu.bookit.application.port.out.nofication.NotificationPort;
import com.opencu.bookit.domain.model.event.EventNotification;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class NotificationPortDispatcher implements NotificationPort {
    private final NotificationPort emailAdapter;

    public NotificationPortDispatcher(EmailProvider emailAdapter) {
        this.emailAdapter = emailAdapter;
    }

    @Override
    public void sendNotification(EventNotification notification) {
        resolveActiveAdapters().forEach(adapter -> adapter.sendNotification(notification));
    }

    private List<NotificationPort> resolveActiveAdapters() {
        List<NotificationPort> activeAdapters = new ArrayList<>();
        if (AppFeatures.EMAIL_NOTIFICATIONS.isActive()) {
            activeAdapters.add(emailAdapter);
        }
        return activeAdapters;
    }
}