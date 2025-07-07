package ru.tbank.bookit.book_it_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.DTO.*;
import ru.tbank.bookit.book_it_backend.repository.BookingStatsRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final BookingStatsRepository bookingStatsRepository;

    public List<BookingStatsResponse> getBookingStats(LocalDate startDate,
                                                      LocalDate endDate) {
        List<Object[]> results = bookingStatsRepository.findBookingStatsBetweenDates(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        return results.stream()
                .map(result -> new BookingStatsResponse(
                        ((Date) result[0]).toLocalDate(),
                        (String) result[1],
                        ((Number) result[2]).longValue(),
                        null
                ))
                .collect(Collectors.toList());
    }

    public StatsSummaryResponse getStatsSummary(LocalDate startDate,
                                                LocalDate endDate,
                                                List<BookingStatsResponse> stats) {
        if (stats == null || stats.isEmpty()) {
            return new StatsSummaryResponse(0, null, 0, null, List.of());
        }

        long totalBookings = stats.stream()
                .mapToLong(BookingStatsResponse::totalBookings)
                .sum();

        Map<String, Long> areaBookings = stats.stream()
                .collect(Collectors.groupingBy(
                        BookingStatsResponse::areaName,
                        Collectors.summingLong(BookingStatsResponse::totalBookings)
                ));

        String mostPopularArea = areaBookings.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Map<LocalDate, Long> dailyBookings = stats.stream()
                .collect(Collectors.groupingBy(
                        BookingStatsResponse::date,
                        Collectors.summingLong(BookingStatsResponse::totalBookings)
                ));

        Map.Entry<LocalDate, Long> peakDay = dailyBookings.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        List<AreaStats> areaStatsList = areaBookings.entrySet().stream()
                .map(entry -> new AreaStats(
                        entry.getKey(),
                        entry.getValue(),
                        totalBookings > 0 ? (double) entry.getValue() / totalBookings * 100 : 0
                ))
                .sorted(Comparator.comparingLong(AreaStats::totalBookings).reversed())
                .collect(Collectors.toList());

        return new StatsSummaryResponse(
                totalBookings,
                mostPopularArea,
                peakDay != null ? peakDay.getValue() : 0,
                peakDay != null ? peakDay.getKey() : null,
                areaStatsList
        );
    }

    public List<DayOfWeekStatsResponse> getBookingStatsByDayOfWeek(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = bookingStatsRepository.findBookingStatsByDayOfWeek(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        return results.stream()
                .map(result -> new DayOfWeekStatsResponse(
                        (String) result[0],
                        ((Number) result[1]).longValue(),
                        (String) result[2]
                ))
                .collect(Collectors.toList());
    }

    public List<CancellationStatsResponse> getCancellationStatsByArea(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = bookingStatsRepository.findCancellationStatsByArea(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        return results.stream()
                .map(result -> new CancellationStatsResponse(
                        (String) result[0],
                        ((Number) result[1]).longValue(),
                        ((Number) result[2]).longValue(),
                        ((Number) result[1]).longValue() > 0
                                ? ((Number) result[2]).doubleValue() / ((Number) result[1]).doubleValue() * 100
                                : 0
                ))
                .collect(Collectors.toList());
    }

    public List<BusiestHoursResponse> getBusiestHoursStats(
            LocalDateTime start,
            LocalDateTime end,
            String areaName) {

        List<Object[]> results = bookingStatsRepository.findBusiestHours(
                start, end, areaName);

        return results.stream()
                .map(result -> new BusiestHoursResponse(
                        (Integer) result[0], // hour
                        ((Number) result[1]).longValue() // bookings count
                ))
                .sorted(Comparator.comparing(BusiestHoursResponse::hour))
                .collect(Collectors.toList());
    }
}