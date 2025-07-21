package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.application.service.nofication.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationControllerV1 {

    private final NotificationService notificationService;


    @PostMapping("/unsubscribe/{userId}")
    public ResponseEntity<String> unsubscribe(@PathVariable UUID userId) {
        notificationService.unsubscribeFromNotifications(userId);
        return ResponseEntity.ok("You have successfully unsubscribed from notifications");
    }

    @PostMapping("/subscribe/{userId}")
    public ResponseEntity<String> subscribe(@PathVariable UUID userId) {
        if (notificationService.isSubscribedToNotifications(userId)) {
            return ResponseEntity.badRequest().body("You are already subscribed to notifications");
        }
        notificationService.subscribeToNotifications(userId);
        return ResponseEntity.ok("You have successfully subscribed to notifications");
    }
}