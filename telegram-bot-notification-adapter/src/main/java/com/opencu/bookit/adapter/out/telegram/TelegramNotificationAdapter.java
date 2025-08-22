package com.opencu.bookit.adapter.out.telegram;

import com.opencu.bookit.adapter.out.telegram.BotImplementation.MyTelegramBot;
import com.opencu.bookit.adapter.out.telegram.spi.TelegramProvider;
import com.opencu.bookit.domain.model.event.EventNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "tg-bot", name = "enabled", havingValue = "true")
public class TelegramNotificationAdapter implements TelegramProvider {
    private final MyTelegramBot myTelegramBot;

    public TelegramNotificationAdapter(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    @Override
    public void sendNotification(EventNotification notification) {
        log.info("Sending telegram notification to user: {} for event: {}",
                notification.getUserId(), notification.getEventId());

        String message = String.format("Напоминание о мероприятии: %s\n%s", notification.getEventTitle(), notification.getMessage());
        myTelegramBot.sendMessage(String.valueOf(notification.getUserTgId()), message);
    }
}
