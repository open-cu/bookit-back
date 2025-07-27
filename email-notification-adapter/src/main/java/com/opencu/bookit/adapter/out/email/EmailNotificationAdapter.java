package com.opencu.bookit.adapter.out.email;

import com.opencu.bookit.application.port.out.nofication.NotificationPort;
import com.opencu.bookit.domain.model.event.EventNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailNotificationAdapter implements NotificationPort {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from-email}")
    private String fromEmail;

    @Value("${spring.mail.sender-name}")
    private String senderName;

    @Autowired
    public EmailNotificationAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

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
            log.error("Failed to send email notification: {}", e.getMessage(), e);
            if (e.getMessage().contains("invalid mail data")) {
                log.error("Ошибка формата данных письма. Проверьте формат адресов и содержимого");
            } else if (e.getMessage().contains("Could not connect")) {
                log.error("Проблема подключения к SMTP-серверу. Проверьте сетевые настройки");
            }
        }
    }
}