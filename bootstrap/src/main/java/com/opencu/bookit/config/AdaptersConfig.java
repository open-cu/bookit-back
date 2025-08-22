package com.opencu.bookit.config;

import com.opencu.bookit.adapter.out.email.EmailNotificationAdapter;
import com.opencu.bookit.adapter.out.telegram.TelegramNotificationAdapter;
import com.opencu.bookit.application.feature.AppFeatures;
import com.opencu.bookit.application.port.out.nofication.NotificationPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AdaptersConfig {

    @Autowired
    private ObjectProvider<TelegramNotificationAdapter> telegramAdapterProvider;

    @Bean
    public Map<AppFeatures, NotificationPort> notificationAdapters(
            EmailNotificationAdapter emailAdapter
    ) {
        Map<AppFeatures, NotificationPort> adapters = new HashMap<>();
        adapters.put(AppFeatures.EMAIL_NOTIFICATIONS, emailAdapter);
        telegramAdapterProvider.ifAvailable(adapter ->
                adapters.put(AppFeatures.TELEGRAM_NOTIFICATIONS, adapter)
        );

        return adapters;
    }
}
