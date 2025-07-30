package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.out.security.spring.service.AuthService;
import com.opencu.bookit.application.service.nofication.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationControllerV1 {

    private final NotificationService notificationService;
    private final AuthService authService;

    @PostMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe() {
        UUID userId = authService.getCurrentUser().getId();
        if (!notificationService.isSubscribedToNotifications(userId)) {
            return ResponseEntity.badRequest().body("You are already unsubscribed from notifications");
        }
        notificationService.unsubscribeFromNotifications(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("You have successfully unsubscribed from notifications");
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe() {
        UUID userId = authService.getCurrentUser().getId();
        if (notificationService.isSubscribedToNotifications(userId)) {
            return ResponseEntity.badRequest().body("You are already subscribed to notifications");
        }
        notificationService.subscribeToNotifications(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("You have successfully subscribed to notifications");
    }

    @GetMapping("/check-subscription")
    public ResponseEntity<Boolean> checkSubscription() {
        UUID userId = authService.getCurrentUser().getId();
        boolean isSubscribed = notificationService.isSubscribedToNotifications(userId);
        return ResponseEntity.ok(isSubscribed);
    }
}