package com.opencu.bookit.adapter.out.nofication;

import com.opencu.bookit.application.port.out.nofication.DeleteDelayedNotificationPort;
import com.opencu.bookit.application.port.out.nofication.NotificationQueuePort;
import com.opencu.bookit.domain.model.event.EventNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQNotificationAdapter implements NotificationQueuePort {

    private final RabbitTemplate rabbitTemplate;
    DeleteDelayedNotificationPort deleteDelayedNotificationPort;

    @Value("${rabbitmq.exchange.delayed}")
    private String delayedExchangeName;

    @Value("${rabbitmq.queue.notifications}")
    private String notificationsQueueName;

    @Value("${rabbitmq.queue.routing-key}")
    public String routingKey;

    @Override
    public void scheduleNotification(EventNotification notification, long delayInMilliseconds) {
        log.info("Scheduling notification for event: {} with delay: {} ms", 
                notification.getEventId(), delayInMilliseconds);
        
        rabbitTemplate.convertAndSend(
                delayedExchangeName,
                routingKey,
                notification,
                message -> {
                    message.getMessageProperties().setDelayLong(delayInMilliseconds);
                    return message;
                }
        );
    }
}