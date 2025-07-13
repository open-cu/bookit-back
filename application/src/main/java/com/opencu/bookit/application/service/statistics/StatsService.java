package com.opencu.bookit.application.service.statistics;

import com.opencu.bookit.application.port.out.statstics.LoadBookingStatsPort;
import com.opencu.bookit.domain.model.statistics.BookingStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final LoadBookingStatsPort loadBookingStatsPort;

    public List<BookingStats> getBookingStats(LocalDate startDate,
                                              LocalDate endDate) {
        List<Object[]> results = loadBookingStatsPort.findBookingStatsBetweenDates(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
                                                                                  );

        return results.stream()
                .map(result -> new BookingStats(
                        ((Date) result[0]).toLocalDate(),
                        (String) result[1],
                        ((Number) result[2]).longValue()
                ))
                .collect(Collectors.toList());
    }
}