package com.opencu.bookit.adapter.out.nofication;

import com.opencu.bookit.application.port.out.nofication.NotificationQueuePort;
import com.opencu.bookit.domain.model.event.EventNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQNotificationAdapter implements NotificationQueuePort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.delayed}")
    private String delayedExchangeName;

    @Value("${rabbitmq.queue.notifications}")
    private String notificationsQueueName;

    @Override
    public void scheduleNotification(EventNotification notification, long delayInMilliseconds) {
        log.info("Scheduling notification for event: {} with delay: {} ms", 
                notification.getEventId(), delayInMilliseconds);
        
        rabbitTemplate.convertAndSend(
                delayedExchangeName,
                notificationsQueueName,
                notification,
                message -> {
                    message.getMessageProperties().setDelayLong(delayInMilliseconds);
                    message.getMessageProperties().setMessageId(notification.getId().toString());
                    return message;
                }
        );
    }

    @Override
    public void cancelNotification(UUID notificationId) {
        //TODO отмена посредством изменения статуса сообщений в бд
    }
}