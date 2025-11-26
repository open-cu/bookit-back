package com.opencu.bookit.application.service.event;

import com.opencu.bookit.application.port.in.booking.CRUDBookingUseCase;
import com.opencu.bookit.application.port.out.area.LoadAreaPort;
import com.opencu.bookit.application.port.out.event.DeleteEventPort;
import com.opencu.bookit.application.port.out.event.LoadEventApplicationPort;
import com.opencu.bookit.application.port.out.event.LoadEventPort;
import com.opencu.bookit.application.port.out.event.SaveEventPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.service.booking.BookingService;
import com.opencu.bookit.application.service.nofication.NotificationService;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.event.*;
import com.opencu.bookit.domain.model.user.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class EventServiceTest {

    @Mock private LoadEventPort loadEventPort;
    @Mock private SaveEventPort saveEventPort;
    @Mock private LoadUserPort loadUserPort;
    @Mock private DeleteEventPort deleteEventPort;
    @Mock private NotificationService notificationService;
    @Mock private BookingService bookingService;
    @Mock private LoadAreaPort loadAreaPort;
    @Mock private LoadEventApplicationPort loadEventApplicationPort;

    @InjectMocks private EventService service;

    @BeforeEach
    void setup() throws Exception {
        // Inject ZoneId and default days via reflection since they are @Value fields
        var zoneIdField = EventService.class.getDeclaredField("zoneId");
        zoneIdField.setAccessible(true);
        zoneIdField.set(service, ZoneId.of("UTC"));
        var daysField = EventService.class.getDeclaredField("defaultTimeBeforeEventInDays");
        daysField.setAccessible(true);
        daysField.set(service, 1);
    }

    @Test
    @DisplayName("getStatus: COMPLETED when endTime before now")
    void getStatus_completed() {
        EventModel e = new EventModel();
        e.setId(UUID.randomUUID());
        e.setEndTime(LocalDateTime.now(ZoneId.of("UTC")).minusHours(1));

        assertEquals(EventStatus.COMPLETED, service.getStatus(UUID.randomUUID(), e));
    }

    @Test
    @DisplayName("getStatus: REGISTERED when user already in participants")
    void getStatus_registered() {
        UUID userId = UUID.randomUUID();
        EventModel e = new EventModel();
        e.setId(UUID.randomUUID());
        e.setEndTime(LocalDateTime.now(ZoneId.of("UTC")).plusHours(1));
        UserModel u = new UserModel(); u.setId(userId);
        e.getUserModels().add(u);

        assertEquals(EventStatus.REGISTERED, service.getStatus(userId, e));
    }

    @Test
    @DisplayName("getStatus with application required: APPLICATION_SENT or AVAILABLE or REGISTRATION_CLOSED")
    void getStatus_applicationRequired() {
        UUID userId = UUID.randomUUID();
        EventModel e = new EventModel();
        e.setId(UUID.randomUUID());
        e.setEndTime(LocalDateTime.now(ZoneId.of("UTC")).plusDays(1));
        e.setRequiresApplication(true);

        when(loadEventApplicationPort.findByUserIdAndEventId(userId, e.getId())).thenReturn(Optional.empty());
        e.setRegistrationDeadline(LocalDateTime.now(ZoneId.of("UTC")).plusHours(1));
        assertEquals(EventStatus.AVAILABLE_FOR_APPLICATION, service.getStatus(userId, e));

        e.setRegistrationDeadline(LocalDateTime.now(ZoneId.of("UTC")).minusHours(1));
        assertEquals(EventStatus.REGISTRATION_CLOSED, service.getStatus(userId, e));

        EventApplicationModel app = new EventApplicationModel();
        app.setStatus(EventApplicationStatus.APPROVED);
        when(loadEventApplicationPort.findByUserIdAndEventId(userId, e.getId())).thenReturn(Optional.of(app));
        e.setRegistrationDeadline(null);
        assertEquals(EventStatus.AVAILABLE, service.getStatus(userId, e));

        app.setStatus(EventApplicationStatus.PENDING);
        when(loadEventApplicationPort.findByUserIdAndEventId(userId, e.getId())).thenReturn(Optional.of(app));
        assertEquals(EventStatus.APPLICATION_SENT, service.getStatus(userId, e));
    }

    @Test
    @DisplayName("getStatus without application: FULL vs AVAILABLE based on places and registration deadline")
    void getStatus_noApplication_full_or_available() {
        UUID userId = UUID.randomUUID();
        EventModel e = new EventModel();
        e.setId(UUID.randomUUID());
        e.setEndTime(LocalDateTime.now(ZoneId.of("UTC")).plusHours(2));
        e.setRequiresApplication(false);
        e.setRegistrationDeadline(LocalDateTime.now(ZoneId.of("UTC")).plusHours(1));

        e.setAvailable_places(0);
        assertEquals(EventStatus.FULL, service.getStatus(userId, e));

        e.setAvailable_places(5);
        assertEquals(EventStatus.AVAILABLE, service.getStatus(userId, e));

        e.setRegistrationDeadline(LocalDateTime.now(ZoneId.of("UTC")).minusHours(1));
        assertEquals(EventStatus.REGISTRATION_CLOSED, service.getStatus(userId, e));
    }

    @Test
    @DisplayName("addUser: sends immediate notification when event is soon")
    void addUser_immediateNotification() {
        UUID userId = UUID.randomUUID();
        UUID areaId = UUID.randomUUID();
        EventModel e = new EventModel();
        e.setId(UUID.randomUUID());
        e.setName("E");
        e.setStartTime(LocalDateTime.now(ZoneId.of("UTC")).plusHours(2));
        e.setEndTime(e.getStartTime().plusHours(1));
        e.setAvailable_places(2);
        e.setAreaModel(new AreaModel());
        e.getAreaModel().setId(areaId);
        e.setRequiresApplication(false);
        e.setRegistrationDeadline(LocalDateTime.now(ZoneId.of("UTC")).plusHours(1));

        when(loadUserPort.findById(userId)).thenReturn(Optional.of(new UserModel()));
        when(saveEventPort.save(e)).thenReturn(e);

        service.addUser(userId, e);

        assertEquals(1, e.getAvailable_places());
        verify(saveEventPort).save(e);
        verify(notificationService).sendEventNotificationNow(any());
        verify(notificationService, never()).scheduleEventNotification(any(), any());
    }

    @Test
    @DisplayName("addUser: validates status AVAILABLE, saves event, creates booking and schedules notification")
    void addUser_happyPath() {
        UUID userId = UUID.randomUUID();
        UUID areaId = UUID.randomUUID();
        EventModel e = new EventModel();
        e.setId(UUID.randomUUID());
        e.setName("E");
        e.setStartTime(LocalDateTime.now(ZoneId.of("UTC")).plusDays(2));
        e.setEndTime(e.getStartTime().plusHours(2));
        e.setAvailable_places(5);
        e.setAreaModel(new AreaModel());
        e.getAreaModel().setId(areaId);

        e.setRequiresApplication(false);
        e.setRegistrationDeadline(LocalDateTime.now(ZoneId.of("UTC")).plusDays(1));

        when(loadUserPort.findById(userId)).thenReturn(Optional.of(new UserModel()));
        when(saveEventPort.save(e)).thenReturn(e);

        service.addUser(userId, e);

        assertEquals(4, e.getAvailable_places());
        verify(saveEventPort).save(e);

        @SuppressWarnings("unchecked") ArgumentCaptor<Set> rulesCaptor = ArgumentCaptor.forClass(Set.class);
        ArgumentCaptor<CRUDBookingUseCase.CreateBookingCommand> cmdCaptor = ArgumentCaptor.forClass(CRUDBookingUseCase.CreateBookingCommand.class);
        verify(bookingService).createBooking(cmdCaptor.capture(), rulesCaptor.capture());

        CRUDBookingUseCase.CreateBookingCommand cmd = cmdCaptor.getValue();
        assertEquals(userId, cmd.userId());
        assertEquals(areaId, cmd.areaId());
        assertTrue(cmd.eventId().isPresent());
        assertEquals(e.getId(), cmd.eventId().get());
        assertTrue(cmd.timePeriods().contains(org.springframework.data.util.Pair.of(e.getStartTime(), e.getEndTime())));

        @SuppressWarnings("unchecked") Set<com.opencu.bookit.domain.model.booking.ValidationRule> rules = rulesCaptor.getValue();
        assertEquals(Set.of(com.opencu.bookit.domain.model.booking.ValidationRule.VALIDATE_TIME_RESTRICTIONS), rules);

        verify(notificationService).scheduleEventNotification(any(), any());
    }

    @Test
    @DisplayName("addUser: throws when status not AVAILABLE")
    void addUser_notAvailable_throws() {
        EventModel e = new EventModel();
        e.setId(UUID.randomUUID());
        e.setName("E");
        e.setEndTime(LocalDateTime.now(ZoneId.of("UTC")).minusHours(1));

        assertThrows(IllegalArgumentException.class, () -> service.addUser(UUID.randomUUID(), e));
        verifyNoInteractions(loadUserPort, saveEventPort, bookingService, notificationService);
    }

    @Test
    @DisplayName("removeUser: removes participant and cancels booking/notification")
    void removeUser_happyPath() {
        UUID userId = UUID.randomUUID();
        UserModel u = new UserModel(); u.setId(userId);
        EventModel e = new EventModel();
        e.setId(UUID.randomUUID());
        e.setStartTime(LocalDateTime.now(ZoneId.of("UTC")).plusDays(1));
        e.setEndTime(e.getStartTime().plusHours(1));
        e.setAreaModel(new AreaModel()); e.getAreaModel().setId(UUID.randomUUID());
        e.getUserModels().add(u);
        e.setAvailable_places(0);

        when(loadUserPort.findById(userId)).thenReturn(Optional.of(u));

        service.removeUser(userId, e);

        assertEquals(1, e.getAvailable_places());
        verify(saveEventPort).save(e);
        verify(bookingService).deleteBookingAccordingToIndirectParameters(eq(userId), any(), any(), any());
        verify(notificationService).cancelNotification(userId, e.getId());
    }
}
