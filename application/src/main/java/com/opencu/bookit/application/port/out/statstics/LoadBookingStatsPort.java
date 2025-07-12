package com.opencu.bookit.application.port.out.statstics;

import java.time.LocalDateTime;
import java.util.List;

public interface LoadBookingStatsPort {
    List<Object[]> findBookingStatsBetweenDates(LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
