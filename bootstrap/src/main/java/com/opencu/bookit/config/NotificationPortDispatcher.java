package com.opencu.bookit.config;

import com.opencu.bookit.adapter.out.email.spi.EmailProvider;
import com.opencu.bookit.adapter.out.telegram.spi.TelegramProvider;
import com.opencu.bookit.application.feature.AppFeatures;
import com.opencu.bookit.application.port.out.nofication.NotificationPort;
import com.opencu.bookit.domain.model.event.EventNotification;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;

@Component
@Primary
public class NotificationPortDispatcher implements NotificationPort {

    private final Map<AppFeatures, NotificationPort> adapters = new HashMap<>();

    @Autowired
    public NotificationPortDispatcher(ObjectProvider<EmailProvider> emailProvider, ObjectProvider<TelegramProvider> telegramAdapterProvider) {
        telegramAdapterProvider.ifAvailable(adapter -> adapters.put(AppFeatures.TELEGRAM_NOTIFICATIONS, adapter));
        emailProvider.ifAvailable(adapter -> adapters.put(AppFeatures.EMAIL_NOTIFICATIONS, adapter));
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