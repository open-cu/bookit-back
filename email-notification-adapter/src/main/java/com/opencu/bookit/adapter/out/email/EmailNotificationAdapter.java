package com.opencu.bookit.adapter.out.email;

import com.opencu.bookit.application.port.out.nofication.NotificationPort;
import com.opencu.bookit.domain.model.event.EventNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationAdapter implements NotificationPort {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.sender-name}")
    private String senderName;

    @Override
    public void sendNotification(EventNotification notification) {
        log.info("Sending email notification to user: {} for event: {}",
                notification.getUserId(), notification.getEventId());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(String.format("%s <%s>", senderName, fromEmail));
        message.setTo(notification.getUserEmail());
        message.setSubject("Напоминание о мероприятии: " + notification.getEventTitle());
        message.setText(notification.getMessage());

        try {
            mailSender.send(message);
            log.info("Email notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send email notification", e);
        }
    }
}