package ru.tbank.bookit.book_it_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.DTO.*;
import ru.tbank.bookit.book_it_backend.model.HallOccupancy;
import ru.tbank.bookit.book_it_backend.repository.BookingStatsRepository;
import ru.tbank.bookit.book_it_backend.repository.HallOccupancyRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final BookingStatsRepository bookingStatsRepository;
    private final HallOccupancyRepository hallOccupancyRepository;

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

        Map<Integer, Long> hourToCountMap = results.stream()
                .collect(Collectors.toMap(
                        result -> (Integer) result[0],
                        result -> ((Number) result[1]).longValue()
                ));

        return IntStream.rangeClosed(8, 20)
                .mapToObj(hour -> new BusiestHoursResponse(
                        hour,
                        hourToCountMap.getOrDefault(hour, 0L)
                ))
                .sorted(Comparator.comparing(BusiestHoursResponse::hour))
                .collect(Collectors.toList());
    }

    public List<BusiestHoursResponse> getBusiestHoursForHall(
            LocalDate startDate,
            LocalDate endDate) {

        List<LocalDate> datesInRange = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toList());

        List<HallOccupancy> allOccupancies = datesInRange.stream()
                .flatMap(date -> hallOccupancyRepository.findByDate(date).stream())
                .collect(Collectors.toList());

        Map<Integer, Long> hourToCountMap = allOccupancies.stream()
                .collect(Collectors.groupingBy(
                        ho -> ho.getDateTime().getHour(),
                        Collectors.summingLong(HallOccupancy::getReservedPlaces)
                ));

        return IntStream.rangeClosed(8, 20)
                .mapToObj(hour -> new BusiestHoursResponse(
                        hour,
                        hourToCountMap.getOrDefault(hour, 0L)
                ))
                .sorted(Comparator.comparing(BusiestHoursResponse::hour))
                .collect(Collectors.toList());
    }

    public List<EventOverlapResponse> eventOverlapStats() {
        List<Object[]> overlaps = bookingStatsRepository.findEventOverlapPercentage();
        return overlaps.stream().map(overlap ->
                new EventOverlapResponse(
                        (UUID) overlap[0],
                        (String) overlap[1],
                        (UUID) overlap[2],
                        (String) overlap[3],
                        (long) overlap[4],
                        (long) overlap[5],
                        (long) overlap[6],
                        (double) overlap[7]
                        )
                ).collect(Collectors.toList());
    }

    public List<NewUsersCreatedAtResponse> newUsersCreatedAtStats() {
        List<Object[]> newUsers = bookingStatsRepository.findNewUsersByCreatedAtYearMonth();
        return newUsers.stream().map(newUser ->
                new NewUsersCreatedAtResponse(
                (String) newUser[0],
                (long) newUser[1])
        ).collect(Collectors.toList());
    }
}