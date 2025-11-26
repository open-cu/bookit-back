package com.opencu.bookit.application.service.booking;

import com.opencu.bookit.application.config.BookingConfig;
import com.opencu.bookit.application.exception.FeatureUnavailableException;
import com.opencu.bookit.application.feature.AppFeatures;
import com.opencu.bookit.application.port.out.area.LoadAreaPort;
import com.opencu.bookit.application.port.out.booking.LoadBookingPort;
import com.opencu.bookit.application.port.out.schedule.LoadNonWorkingDaySchedulePort;
import com.opencu.bookit.application.port.out.statstics.LoadHallOccupancyPort;
import com.opencu.bookit.application.port.out.user.LoadAuthorizationInfoPort;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaType;
import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.booking.BookingStatus;
import com.opencu.bookit.domain.model.statistics.HallOccupancyModel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock private LoadAreaPort loadAreaPort;
    @Mock private LoadBookingPort loadBookingPort;
    @Mock private LoadNonWorkingDaySchedulePort loadNonWorkingDaySchedulePort;
    @Mock private LoadHallOccupancyPort loadHallOccupancyPort;
    @Mock private LoadAuthorizationInfoPort loadAuthorizationInfoPort;
    @Mock private BookingConfig bookingConfig;

    @InjectMocks private AvailabilityService service;

    private UUID areaId;
    private AreaModel meetingRoom;

    @BeforeEach
    void init() {
        areaId = UUID.randomUUID();
        meetingRoom = new AreaModel();
        meetingRoom.setId(areaId);
        meetingRoom.setType(AreaType.MEETING_ROOM);

        when(bookingConfig.getZoneId()).thenReturn(ZoneId.of("UTC"));
        lenient().when(bookingConfig.getStartWork()).thenReturn(9L);
        lenient().when(bookingConfig.getEndWork()).thenReturn(18L);
        lenient().when(bookingConfig.getHallMaxCapacity()).thenReturn(10);
    }

    @Test
    @DisplayName("findAvailableTime returns empty slots for past date")
    void findAvailableTime_pastDate_returnsEmpty() {
        LocalDate date = LocalDate.now(ZoneId.of("UTC")).minusDays(1);
        List<List<Pair<LocalDateTime, LocalDateTime>>> res = service.findAvailableTime(date, Optional.empty(), Optional.empty());
        assertEquals(3, res.size());
        assertTrue(res.get(0).isEmpty());
        assertTrue(res.get(1).isEmpty());
        assertTrue(res.get(2).isEmpty());
    }

    @Test
    @DisplayName("findAvailableTime throws if meeting spaces disabled and non-workplace requested")
    void findAvailableTime_featureToggle_blocked() {
        LocalDate date = LocalDate.now(ZoneId.of("UTC")).plusDays(1);
        when(loadAreaPort.findById(areaId)).thenReturn(Optional.of(meetingRoom));
        when(loadBookingPort.findByDateAndArea(date, areaId)).thenReturn(List.of());

        try (MockedStatic<AppFeatures> app = mockStatic(AppFeatures.class, CALLS_REAL_METHODS)) {
            app.when(() -> AppFeatures.BOOKING_MEETING_SPACES.isActive()).thenReturn(false);
            assertThrows(FeatureUnavailableException.class, () -> service.findAvailableTime(date, Optional.of(areaId), Optional.empty()));
        }
    }

    @Test
    @DisplayName("isAreaAvailable false when conflicting confirmed booking exists")
    void isAreaAvailable_conflict() {
        LocalDateTime time = LocalDateTime.of(2030,1,1,10,0);
        when(loadAreaPort.findById(areaId)).thenReturn(Optional.of(meetingRoom));

        BookingModel b = new BookingModel();
        b.setId(UUID.randomUUID());
        b.setStatus(BookingStatus.CONFIRMED);
        AreaModel a = new AreaModel(); a.setId(areaId); b.setAreaModel(a);
        b.setStartTime(time.minusMinutes(30));
        b.setEndTime(time.plusMinutes(30));
        when(loadBookingPort.findAllIncludingTime(time)).thenReturn(List.of(b));

        assertFalse(service.isAreaAvailable(areaId, new HashSet<>(Collections.singleton(time)), Optional.empty()));
    }

    @Test
    @DisplayName("findAvailableAreas includes workplace only if capacity allows")
    void findAvailableAreas_workplaceLogic() {
        UUID workplaceId = UUID.randomUUID();
        AreaModel workplace = new AreaModel(); workplace.setId(workplaceId); workplace.setType(AreaType.WORKPLACE);
        AreaModel other = new AreaModel(); other.setId(areaId); other.setType(AreaType.MEETING_ROOM);

        when(loadAreaPort.findAll()).thenReturn(List.of(other, workplace));
        when(loadAreaPort.findByType(AreaType.WORKPLACE)).thenReturn(List.of(workplace));

        try (MockedStatic<AppFeatures> app = mockStatic(AppFeatures.class, CALLS_REAL_METHODS)) {
            app.when(() -> AppFeatures.BOOKING_MEETING_SPACES.isActive()).thenReturn(true);

            LocalDateTime t = LocalDateTime.of(2030,1,1,10,0);
            when(loadNonWorkingDaySchedulePort.findByDate(t.toLocalDate())).thenReturn(Optional.empty());
            when(loadHallOccupancyPort.getByDateTime(t)).thenReturn(Optional.of(new HallOccupancyModel(t, 0)));

            List<UUID> available = service.findAvailableAreas(Set.of(t));
            assertTrue(available.contains(workplaceId));
            assertTrue(available.contains(areaId));
        }
    }

    @Test
    @DisplayName("findAvailableDates throws FeatureUnavailable when meeting spaces disabled and area not workplace")
    void findAvailableDates_featureToggle() {
        when(loadAreaPort.findById(areaId)).thenReturn(Optional.of(meetingRoom));
        try (MockedStatic<AppFeatures> app = mockStatic(AppFeatures.class, CALLS_REAL_METHODS)) {
            app.when(() -> AppFeatures.BOOKING_MEETING_SPACES.isActive()).thenReturn(false);
            assertThrows(FeatureUnavailableException.class, () -> service.findAvailableDates(Optional.of(areaId)));
        }
    }
}
