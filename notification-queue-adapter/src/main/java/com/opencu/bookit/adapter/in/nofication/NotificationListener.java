package com.opencu.bookit.adapter.in.nofication;

import com.opencu.bookit.application.port.out.nofication.DeleteDelayedNotificationPort;
import com.opencu.bookit.application.port.out.nofication.LoadDelayedEventNotificationPort;
import com.opencu.bookit.application.service.nofication.NotificationService;
import com.opencu.bookit.domain.model.event.EventNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationService notificationService;
    private final LoadDelayedEventNotificationPort loadDelayedEventNotificationPort;
    private final DeleteDelayedNotificationPort deleteDelayedNotificationPort;

    @RabbitListener(queues = "${rabbitmq.queue.notifications}")
    public void handleNotification(EventNotification notification) {
        Optional<EventNotification> delayedNotification = loadDelayedEventNotificationPort.loadById(notification.getId());
        if (delayedNotification.isEmpty()) {
            log.warn("Received notification with ID {} was canceled earlier or forged", notification.getId());
            return;
        }

        if (delayedNotification.get().equals(notification)) {
            log.info("Received notification for processing: {}", notification.getId());
            notificationService.sendEventNotificationNow(notification);
            deleteDelayedNotificationPort.cancelNotification(notification.getUserId(), notification.getEventId());
        }
        else {
            log.warn("Received notification does not match saved notification: {}. Expected: {}, Received: {}",
                    notification.getId(), delayedNotification, notification);
        }
    }
}