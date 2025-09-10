package com.opencu.bookit.domain.model.schedule;

import java.time.LocalTime;

public record WorkingDayConfig (
        LocalTime workingDayStart,
        LocalTime workingDayEnd,
        Long bookingSlotDuration,
        Boolean isDayOff) {
}