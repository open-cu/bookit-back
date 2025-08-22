package com.opencu.bookit.config;

import com.opencu.bookit.adapter.out.email.spi.EmailProvider;
import com.opencu.bookit.application.feature.AppFeatures;
import com.opencu.bookit.application.port.out.nofication.NotificationPort;
import com.opencu.bookit.domain.model.event.EventNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;


import java.util.Map;

@Component
@Primary
public class NotificationPortDispatcher implements NotificationPort {

    @Autowired
    private final Map<AppFeatures, NotificationPort> adapters;

    public NotificationPortDispatcher(Map<AppFeatures, NotificationPort> adapters) {
        this.adapters = adapters;
    }

    @Override
    public void sendNotification(EventNotification notification) {
        adapters.forEach((feature, adapter) -> {
            if (feature.isActive()) {
                adapter.sendNotification(notification);
            }
        });
    }
}