package com.opencu.bookit.application.service.statistics;

import com.opencu.bookit.application.port.out.statstics.LoadBookingStatsPort;
import com.opencu.bookit.application.port.out.statstics.LoadHallOccupancyPort;
import com.opencu.bookit.domain.model.statistics.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock private LoadBookingStatsPort loadBookingStatsPort;
    @Mock private LoadHallOccupancyPort loadHallOccupancyPort;

    @InjectMocks private StatsService service;

    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setup() {
        startDate = LocalDate.of(2030, 1, 1);
        endDate = LocalDate.of(2030, 1, 10);

        ReflectionTestUtils.setField(service, "startWorkHour", 9);
        ReflectionTestUtils.setField(service, "endWorkHour", 18);
    }

    @Test
    @DisplayName("getBookingStats maps DB rows to BookingStats and switches by areaNames presence")
    void getBookingStats_ok() {
        LocalDateTime s = startDate.atStartOfDay();
        LocalDateTime e = endDate.atTime(23, 59, 59);
        List<Object[]> rows = List.of(
                new Object[]{Date.valueOf(startDate), "Area A", 5L},
                new Object[]{Date.valueOf(startDate.plusDays(1)), "Area B", 3L}
        );
        when(loadBookingStatsPort.findBookingStatsBetweenDates(s, e)).thenReturn(rows);

        List<BookingStats> stats = service.getBookingStats(startDate, endDate, Collections.emptyList());
        assertEquals(2, stats.size());
        assertEquals(startDate, stats.get(0).date());
        assertEquals("Area A", stats.get(0).areaName());
        assertEquals(5L, stats.get(0).totalBookings());
        verify(loadBookingStatsPort).findBookingStatsBetweenDates(s, e);

        // With area names
        List<String> areas = List.of("Area A");
        when(loadBookingStatsPort.findBookingStatsBetweenDatesAndAreas(s, e, areas)).thenReturn(rows);
        List<BookingStats> stats2 = service.getBookingStats(startDate, endDate, areas);
        assertEquals(2, stats2.size());
        verify(loadBookingStatsPort).findBookingStatsBetweenDatesAndAreas(s, e, areas);
    }

    @Test
    @DisplayName("getStatsSummary returns zeros when stats empty")
    void getStatsSummary_empty() {
        StatsSummary summary = service.getStatsSummary(startDate, endDate, List.of());
        assertEquals(0L, summary.totalBookings());
        assertNull(summary.mostPopularArea());
        assertEquals(0L, summary.maxBookingsInDay());
        assertNull(summary.peakDate());
        assertTrue(summary.areaStats().isEmpty());
    }

    @Test
    @DisplayName("getStatsSummary aggregates totals, most popular area, peak day, and area percentages")
    void getStatsSummary_aggregates() {
        List<BookingStats> input = List.of(
                new BookingStats(startDate, "Area A", 5L),
                new BookingStats(startDate.plusDays(1), "Area A", 3L),
                new BookingStats(startDate.plusDays(1), "Area B", 7L)
        );
        StatsSummary summary = service.getStatsSummary(startDate, endDate, input);
        assertEquals(15L, summary.totalBookings());
        assertEquals("Area B", summary.mostPopularArea());
        assertEquals(10L, summary.maxBookingsInDay());
        assertEquals(startDate.plusDays(1), summary.peakDate());
        assertEquals(2, summary.areaStats().size());

        AreaStats top = summary.areaStats().get(0);
        assertEquals("Area B", top.areaName());
        assertEquals(7L, top.totalBookings());
        assertTrue(top.percentageOfTotal() > 45.0 && top.percentageOfTotal() < 50.0);
    }

    @Test
    @DisplayName("getBookingStatsByDayOfWeek maps rows to DayOfWeekStats and switches by areas presence")
    void getBookingStatsByDayOfWeek_ok() {
        LocalDateTime s = startDate.atStartOfDay();
        LocalDateTime e = endDate.atTime(23, 59, 59);
        List<Object[]> rows = List.of(
                new Object[]{"MONDAY", 12L, "Area A"},
                new Object[]{"TUESDAY", 7, "Area B"}
        );
        when(loadBookingStatsPort.findBookingStatsByDayOfWeek(s, e)).thenReturn(rows);
        List<DayOfWeekStats> list = service.getBookingStatsByDayOfWeek(startDate, endDate, null);
        assertEquals(2, list.size());
        assertEquals("MONDAY", list.get(0).dayOfWeek());
        assertEquals(12L, list.get(0).totalBookings());
        assertEquals("Area A", list.get(0).areaName());
        verify(loadBookingStatsPort).findBookingStatsByDayOfWeek(s, e);

        List<String> areas = List.of("Area A");
        when(loadBookingStatsPort.findBookingStatsByDayOfWeekAndAreas(s, e, areas)).thenReturn(rows);
        List<DayOfWeekStats> list2 = service.getBookingStatsByDayOfWeek(startDate, endDate, areas);
        assertEquals(2, list2.size());
        verify(loadBookingStatsPort).findBookingStatsByDayOfWeekAndAreas(s, e, areas);
    }

    @Test
    @DisplayName("getCancellationStatsByArea maps rows to CancellationStats")
    void getCancellationStatsByArea_ok() {
        LocalDateTime s = startDate.atStartOfDay();
        LocalDateTime e = endDate.atTime(23, 59, 59);
        List<Object[]> rows = List.of(
                new Object[]{"Area A", 10L, 2L},
                new Object[]{"Area B", 0L, 0L}
        );
        when(loadBookingStatsPort.findCancellationStatsByArea(s, e)).thenReturn(rows);
        List<CancellationStats> list = service.getCancellationStatsByArea(startDate, endDate, List.of());
        assertEquals(2, list.size());
        assertEquals("Area A", list.get(0).areaName());
        assertEquals(10L, list.get(0).totalBookings());
        assertEquals(2L, list.get(0).cancelledBookings());
        assertEquals(20.0, list.get(0).cancellationPercentage());

        verify(loadBookingStatsPort).findCancellationStatsByArea(s, e);

        List<String> areas = List.of("Area A");
        when(loadBookingStatsPort.findCancellationStatsByAreaAndNames(s, e, areas)).thenReturn(rows);
        List<CancellationStats> list2 = service.getCancellationStatsByArea(startDate, endDate, areas);
        assertEquals(2, list2.size());
        verify(loadBookingStatsPort).findCancellationStatsByAreaAndNames(s, e, areas);
    }

    @Test
    @DisplayName("getBusiestHoursStats maps hours within working range and fills missing hours with zero")
    void getBusiestHoursStats_ok() {
        LocalDateTime s = LocalDateTime.of(2030, 1, 1, 0, 0);
        LocalDateTime e = LocalDateTime.of(2030, 1, 2, 0, 0);
        List<Object[]> rows = List.of(
                new Object[]{9, 3L},
                new Object[]{11, 7L}
        );
        when(loadBookingStatsPort.findBusiestHoursByArea(s, e)).thenReturn(rows);
        List<BusiestHours> list = service.getBusiestHoursStats(s, e, List.of());
        assertEquals(9, list.get(0).hour());
        assertEquals(3L, list.get(0).bookingsCount());
        assertEquals(10, list.get(1).hour());
        assertEquals(0L, list.get(1).bookingsCount());
        assertEquals(11, list.get(2).hour());
        assertEquals(7L, list.get(2).bookingsCount());
        assertEquals(18 - 9, list.size());
        verify(loadBookingStatsPort).findBusiestHoursByArea(s, e);

        List<String> areas = List.of("Area A");
        when(loadBookingStatsPort.findBusiestHoursByAreaAndNames(s, e, areas)).thenReturn(rows);
        service.getBusiestHoursStats(s, e, areas);
        verify(loadBookingStatsPort).findBusiestHoursByAreaAndNames(s, e, areas);
    }

    @Test
    @DisplayName("getBusiestHoursForHall groups occupancies by hour and fills zero for missing")
    void getBusiestHoursForHall_ok() {
        LocalDate s = LocalDate.of(2030, 1, 1);
        LocalDate e = LocalDate.of(2030, 1, 3);

        HallOccupancyModel ho1 = new HallOccupancyModel(LocalDateTime.of(2030,1,1,9,0), 5);
        HallOccupancyModel ho2 = new HallOccupancyModel(LocalDateTime.of(2030,1,2,10,0), 3);
        when(loadHallOccupancyPort.findByDate(LocalDate.of(2030,1,1))).thenReturn(List.of(ho1));
        when(loadHallOccupancyPort.findByDate(LocalDate.of(2030,1,2))).thenReturn(List.of(ho2));
        when(loadHallOccupancyPort.findByDate(LocalDate.of(2030,1,3))).thenReturn(List.of());

        List<BusiestHours> list = service.getBusiestHoursForHall(s, e);
        assertEquals(9, list.get(0).hour());
        assertEquals(5L, list.get(0).bookingsCount());
        assertEquals(10, list.get(1).hour());
        assertEquals(3L, list.get(1).bookingsCount());
        assertTrue(list.stream().anyMatch(b -> b.hour() == 11 && b.bookingsCount() == 0L));
    }

    @Test
    @DisplayName("eventOverlapStats maps rows depending on provided event IDs")
    void eventOverlapStats_ok() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        byte[] b1 = new byte[]{1,2,3};
        byte[] b2 = new byte[]{4,5,6};
        List<Object[]> rows = List.<Object[]>of(new Object[]{b1, "Event A", b2, "Event B", 10L, 20L, 5L, BigDecimal.valueOf(50.0)});
        when(loadBookingStatsPort.findEventOverlapPercentage(id1, id2)).thenReturn(rows);

        List<EventOverlap> list = service.eventOverlapStats(id1, id2);
        assertEquals(1, list.size());
        assertEquals("Event A", list.get(0).eventName1());
        assertEquals("Event B", list.get(0).eventName2());
        assertEquals(10L, list.get(0).event1TotalUsers());
        assertEquals(20L, list.get(0).event2TotalUsers());
        assertEquals(5L, list.get(0).commonUsersCount());
        assertEquals(BigDecimal.valueOf(50.0), list.get(0).overlapPercentage());

        when(loadBookingStatsPort.findEventOverlapPercentage(id1)).thenReturn(rows);
        service.eventOverlapStats(id1, null);
        verify(loadBookingStatsPort).findEventOverlapPercentage(id1);

        when(loadBookingStatsPort.findEventOverlapPercentage()).thenReturn(rows);
        service.eventOverlapStats(null, null);
        verify(loadBookingStatsPort).findEventOverlapPercentage();
    }

    @Test
    @DisplayName("newUsersCreatedAtStats maps rows to NewUsersCreatedAt")
    void newUsersCreatedAtStats_ok() {
        List<Object[]> rows = List.<Object[]>of(new Object[]{"2030-01", 12L});
        when(loadBookingStatsPort.findNewUsersByCreatedAtYearMonth()).thenReturn(rows);
        List<NewUsersCreatedAt> list = service.newUsersCreatedAtStats();
        assertEquals(1, list.size());
        assertEquals("2030-01", list.get(0).created());
        assertEquals(12L, list.get(0).count());
    }
}
