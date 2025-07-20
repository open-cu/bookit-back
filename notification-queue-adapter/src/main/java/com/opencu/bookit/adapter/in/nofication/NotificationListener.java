package com.opencu.bookit.adapter.in.nofication;

import com.opencu.bookit.application.service.nofication.NotificationService;
import com.opencu.bookit.domain.model.event.EventNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.notifications}")
    public void handleNotification(EventNotification notification) {
        log.info("Received notification for processing: {}", notification.getId());
        notificationService.sendNotificationNow(notification);
    }
}