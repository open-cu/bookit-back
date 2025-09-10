package com.opencu.bookit.application.service.schedule;

import com.opencu.bookit.domain.model.schedule.WorkingDayConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;


@Service
public class ScheduleService {
    LocalTime workingDayStart;
    LocalTime workingDayEnd;
    Long bookingSlotDuration;

    public ScheduleService(@Value("${booking.start-work}") int workingDayStartHour,
                           @Value("${booking.end-work}") int workingDayEndHour,
                           @Value("${booking.booking-slot-duration}") Long bookingSlotDuration) {
        this.workingDayStart = LocalTime.of(workingDayStartHour, 0);
        this.workingDayEnd = LocalTime.of(workingDayEndHour, 0);
        this.bookingSlotDuration = bookingSlotDuration;

    }

    public WorkingDayConfig getWorkingDayConfig(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return new WorkingDayConfig(null, null, null, true);
        }
        return new WorkingDayConfig(workingDayStart, workingDayEnd, bookingSlotDuration, false);
    }
}