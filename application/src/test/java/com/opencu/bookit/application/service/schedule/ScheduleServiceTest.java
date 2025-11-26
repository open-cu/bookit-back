package com.opencu.bookit.application.service.schedule;

import com.opencu.bookit.domain.model.schedule.WorkingDayConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleServiceTest {

    @Test
    @DisplayName("Sunday is marked as non-working with null times and duration")
    void getWorkingDayConfig_sunday() {
        ScheduleService service = new ScheduleService(9, 18, 60L);

        LocalDate date = LocalDate.now();
        while (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            date = date.plusDays(1);
        }
        WorkingDayConfig cfg = service.getWorkingDayConfig(date);
        assertTrue(cfg.isDayOff());
        assertNull(cfg.workingDayStart());
        assertNull(cfg.workingDayEnd());
        assertNull(cfg.bookingSlotDuration());
    }

    @Test
    @DisplayName("Weekday returns configured start, end, and duration")
    void getWorkingDayConfig_weekday() {
        ScheduleService service = new ScheduleService(8, 17, 45L);

        LocalDate date = LocalDate.now();
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(1);
        }

        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(1);
        }
        WorkingDayConfig cfg = service.getWorkingDayConfig(date);
        assertFalse(cfg.isDayOff());
        assertEquals("08:00", cfg.workingDayStart().toString());
        assertEquals("17:00", cfg.workingDayEnd().toString());
        assertEquals(45L, cfg.bookingSlotDuration());
    }
}
