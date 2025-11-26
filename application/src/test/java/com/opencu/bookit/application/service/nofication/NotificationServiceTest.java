package com.opencu.bookit.application.service.nofication;

import com.opencu.bookit.application.port.out.event.LoadEventPort;
import com.opencu.bookit.application.port.out.nofication.DeleteDelayedNotificationPort;
import com.opencu.bookit.application.port.out.nofication.NotificationPort;
import com.opencu.bookit.application.port.out.nofication.NotificationQueuePort;
import com.opencu.bookit.application.port.out.nofication.SaveDelayedEventNotificationPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.port.out.user.UserPreferencesPort;
import com.opencu.bookit.domain.model.event.EventNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationQueuePort notificationQueuePort;
    @Mock private NotificationPort notificationPort;
    @Mock private UserPreferencesPort userPreferencesPort;
    @Mock private DeleteDelayedNotificationPort deleteDelayedNotificationPort;
    @Mock private LoadUserPort loadUserPort;
    @Mock private LoadEventPort loadEventPort;
    @Mock private SaveDelayedEventNotificationPort saveDelayedEventNotificationPort;

    @InjectMocks private NotificationService service;

    private UUID userId;
    private UUID eventId;
    private EventNotification notification;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        notification = new EventNotification();
        notification.setId(UUID.randomUUID());
        notification.setUserId(userId);
        notification.setEventId(eventId);
        notification.setEventTitle("title");
        notification.setMessage("body");
        // set @Value fields
        ReflectionTestUtils.setField(service, "zoneId", ZoneId.of("UTC"));
        ReflectionTestUtils.setField(service, "messagingEnabled", true);
    }

    @Test
    @DisplayName("scheduleEventNotification does nothing when messaging disabled")
    void scheduleEventNotification_messagingDisabled() {
        ReflectionTestUtils.setField(service, "messagingEnabled", false);
        service.scheduleEventNotification(notification, LocalDateTime.now().plusMinutes(5));
        verifyNoInteractions(notificationQueuePort, saveDelayedEventNotificationPort, userPreferencesPort);
    }

    @Test
    @DisplayName("scheduleEventNotification schedules and saves when user subscribed and time in future")
    void scheduleEventNotification_subscribed_future() {
        when(userPreferencesPort.isSubscribedToNotifications(userId)).thenReturn(true);
        LocalDateTime target = LocalDateTime.now().plusMinutes(10);
        service.scheduleEventNotification(notification, target);
        verify(userPreferencesPort).isSubscribedToNotifications(userId);
        verify(notificationQueuePort).scheduleNotification(eq(notification), argThat(delay -> delay >= 0));
        verify(saveDelayedEventNotificationPort).save(notification);
        verifyNoMoreInteractions(notificationQueuePort, saveDelayedEventNotificationPort);
    }

    @Test
    @DisplayName("scheduleEventNotification schedules immediate when time in past")
    void scheduleEventNotification_subscribed_past() {
        when(userPreferencesPort.isSubscribedToNotifications(userId)).thenReturn(true);
        LocalDateTime target = LocalDateTime.now().minusMinutes(1);
        service.scheduleEventNotification(notification, target);
        verify(notificationQueuePort).scheduleNotification(eq(notification), eq(0L));
        verify(saveDelayedEventNotificationPort).save(notification);
    }

    @Test
    @DisplayName("scheduleEventNotification logs and skips when user not subscribed")
    void scheduleEventNotification_notSubscribed() {
        when(userPreferencesPort.isSubscribedToNotifications(userId)).thenReturn(false);
        service.scheduleEventNotification(notification, LocalDateTime.now().plusMinutes(5));
        verify(userPreferencesPort).isSubscribedToNotifications(userId);
        verifyNoInteractions(notificationQueuePort, saveDelayedEventNotificationPort);
    }

    @Test
    @DisplayName("sendEventNotificationNow does nothing when messaging disabled")
    void sendEventNotificationNow_messagingDisabled() {
        ReflectionTestUtils.setField(service, "messagingEnabled", false);
        service.sendEventNotificationNow(notification);
        verifyNoInteractions(loadEventPort, loadUserPort, userPreferencesPort, notificationPort);
    }

    @Test
    @DisplayName("sendEventNotificationNow skips when event missing")
    void sendEventNotificationNow_eventMissing() {
        when(loadEventPort.existsById(eventId)).thenReturn(false);
        service.sendEventNotificationNow(notification);
        verify(loadEventPort).existsById(eventId);
        verifyNoMoreInteractions(loadEventPort);
        verifyNoInteractions(loadUserPort, userPreferencesPort, notificationPort);
    }

    @Test
    @DisplayName("sendEventNotificationNow skips when user missing")
    void sendEventNotificationNow_userMissing() {
        when(loadEventPort.existsById(eventId)).thenReturn(true);
        when(loadUserPort.existsById(userId)).thenReturn(false);
        service.sendEventNotificationNow(notification);
        verify(loadEventPort).existsById(eventId);
        verify(loadUserPort).existsById(userId);
        verifyNoInteractions(userPreferencesPort, notificationPort);
    }

    @Test
    @DisplayName("sendEventNotificationNow sends when subscribed and both exist")
    void sendEventNotificationNow_ok() {
        when(loadEventPort.existsById(eventId)).thenReturn(true);
        when(loadUserPort.existsById(userId)).thenReturn(true);
        when(userPreferencesPort.isSubscribedToNotifications(userId)).thenReturn(true);
        service.sendEventNotificationNow(notification);
        verify(notificationPort).sendNotification(notification);
    }

    @Test
    @DisplayName("sendEventNotificationNow does not send when not subscribed")
    void sendEventNotificationNow_notSubscribed() {
        when(loadEventPort.existsById(eventId)).thenReturn(true);
        when(loadUserPort.existsById(userId)).thenReturn(true);
        when(userPreferencesPort.isSubscribedToNotifications(userId)).thenReturn(false);
        service.sendEventNotificationNow(notification);
        verify(userPreferencesPort).isSubscribedToNotifications(userId);
        verifyNoInteractions(notificationPort);
    }

    @Test
    @DisplayName("cancelNotification delegates to delete port")
    void cancelNotification_ok() {
        service.cancelNotification(userId, eventId);
        verify(deleteDelayedNotificationPort).cancelNotification(userId, eventId);
    }

    @Test
    @DisplayName("unsubscribe and subscribe toggle preferences")
    void preferences_toggle() {
        service.unsubscribeFromNotifications(userId);
        verify(userPreferencesPort).setNotificationPreference(userId, false);
        service.subscribeToNotifications(userId);
        verify(userPreferencesPort).setNotificationPreference(userId, true);
    }

    @Test
    @DisplayName("isSubscribedToNotifications delegates to preferences port")
    void isSubscribedToNotifications_ok() {
        when(userPreferencesPort.isSubscribedToNotifications(userId)).thenReturn(true);
        assertTrue(service.isSubscribedToNotifications(userId));
        verify(userPreferencesPort).isSubscribedToNotifications(userId);
    }
}
