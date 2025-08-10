package com.opencu.bookit.application.service.statistics;

import com.opencu.bookit.application.port.out.statstics.LoadBookingStatsPort;
import com.opencu.bookit.application.port.out.statstics.LoadHallOccupancyPort;
import com.opencu.bookit.domain.model.statistics.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final LoadBookingStatsPort loadBookingStatsPort;
    private final LoadHallOccupancyPort  loadHallOccupancyPort;

    @Value("${booking.start-work}")
    private int startWorkHour;

    @Value("${booking.end-work}")
    private int endWorkHour;

    public List<BookingStats> getBookingStats(LocalDate startDate,
                                              LocalDate endDate,
                                              List<String> areaNames) {
        List<Object[]> results = areaNames == null || areaNames.isEmpty()
                ? loadBookingStatsPort.findBookingStatsBetweenDates(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59))
                : loadBookingStatsPort.findBookingStatsBetweenDatesAndAreas(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59),
                areaNames);

        return results.stream()
                .map(result -> new BookingStats(
                        ((Date) result[0]).toLocalDate(),
                        (String) result[1],
                        ((Long) result[2])
                ))
                .collect(Collectors.toList());
    }

    public StatsSummary getStatsSummary(
            LocalDate startDate,
            LocalDate endDate,
            List<BookingStats> stats
    ) {
        if (stats == null || stats.isEmpty()) {
            return new StatsSummary(0L, null, 0L, null, List.of());
        }

        long totalBookings = stats.stream()
                .mapToLong(BookingStats::totalBookings)
                .sum();

        Map<String, Long> areaBookings = stats.stream()
                .collect(Collectors.groupingBy(
                        BookingStats::areaName,
                        Collectors.summingLong(BookingStats::totalBookings)
                ));

        String mostPopularArea = areaBookings.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Map<LocalDate, Long> dailyBookings = stats.stream()
                .collect(Collectors.groupingBy(
                        BookingStats::date,
                        Collectors.summingLong(BookingStats::totalBookings)
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

        return new StatsSummary(
                totalBookings,
                mostPopularArea,
                peakDay != null ? peakDay.getValue() : 0,
                peakDay != null ? peakDay.getKey() : null,
                areaStatsList
        );
    }

    public List<DayOfWeekStats> getBookingStatsByDayOfWeek(
            LocalDate startDate,
            LocalDate endDate,
            List<String> areaNames
    ) {
        List<Object[]> results = areaNames == null || areaNames.isEmpty()
                ? loadBookingStatsPort.findBookingStatsByDayOfWeek(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59))
                : loadBookingStatsPort.findBookingStatsByDayOfWeekAndAreas(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59),
                areaNames);

        return results.stream()
                .map(result -> new DayOfWeekStats(
                        (String) result[0],
                        ((Number) result[1]).longValue(),
                        (String) result[2]
                ))
                .collect(Collectors.toList());
    }


    public List<CancellationStats> getCancellationStatsByArea(
            LocalDate startDate,
            LocalDate endDate,
            List<String> areaNames
    ) {
        List<Object[]> results = areaNames == null || areaNames.isEmpty()
                ? loadBookingStatsPort.findCancellationStatsByArea(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59))
                : loadBookingStatsPort.findCancellationStatsByAreaAndNames(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59),
                areaNames);

        return results.stream()
                .map(result -> new CancellationStats(
                        (String) result[0],
                        ((Number) result[1]).longValue(),
                        ((Number) result[2]).longValue(),
                        ((Number) result[1]).longValue() > 0
                                ? ((Number) result[2]).doubleValue() / ((Number) result[1]).doubleValue() * 100
                                : 0
                ))
                .collect(Collectors.toList());
    }

    public List<BusiestHours> getBusiestHoursStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> areaNames
    ) {
        List<Object[]> results = areaNames == null || areaNames.isEmpty()
                ? loadBookingStatsPort.findBusiestHoursByArea(start, end)
                : loadBookingStatsPort.findBusiestHoursByAreaAndNames(start, end, areaNames);

        Map<Integer, Long> hourToCountMap = results.stream()
                .collect(Collectors.toMap(
                        result -> (Integer) result[0],
                        result -> ((Number) result[1]).longValue()
                ));

        return IntStream.range(startWorkHour, endWorkHour)
                .mapToObj(hour -> new BusiestHours(
                        hour,
                        hourToCountMap.getOrDefault(hour, 0L)
                ))
                .sorted(Comparator.comparing(BusiestHours::hour))
                .collect(Collectors.toList());
    }


    public List<BusiestHours> getBusiestHoursForHall(
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<LocalDate> datesInRange = startDate.datesUntil(endDate.plusDays(1))
                .toList();

        List<HallOccupancyModel> allOccupancies = datesInRange.stream()
                .flatMap(date -> loadHallOccupancyPort.findByDate(date).stream())
                .toList();

        Map<Integer, Long> hourToCountMap = allOccupancies.stream()
                .collect(Collectors.groupingBy(
                        ho -> ho.getDateTime().getHour(),
                        Collectors.summingLong(HallOccupancyModel::getReservedPlaces)
                ));

        return IntStream.range(startWorkHour, endWorkHour)
                .mapToObj(hour -> new BusiestHours(
                        hour,
                        hourToCountMap.getOrDefault(hour, 0L)
                ))
                .sorted(Comparator.comparing(BusiestHours::hour))
                .collect(Collectors.toList());
    }

    public List<EventOverlap> eventOverlapStats(UUID eventId1, UUID eventId2) {
        List<Object[]> overlaps;

        if (eventId1 != null && eventId2 != null) {
            overlaps = loadBookingStatsPort.findEventOverlapPercentage(eventId1, eventId2);
        } else if (eventId1 != null) {
            overlaps = loadBookingStatsPort.findEventOverlapPercentage(eventId1);
        } else {
            overlaps = loadBookingStatsPort.findEventOverlapPercentage();
        }

        return overlaps.stream().map(overlap ->
                new EventOverlap(
                        bytesToUUID((byte[]) overlap[0]),
                        (String) overlap[1],
                        bytesToUUID((byte[]) overlap[2]),
                        (String) overlap[3],
                        (Long) overlap[4],
                        (Long) overlap[5],
                        (Long) overlap[6],
                        (BigDecimal) overlap[7]
                )
        ).collect(Collectors.toList());
    }

    public List<NewUsersCreatedAt> newUsersCreatedAtStats() {
        List<Object[]> newUsers = loadBookingStatsPort.findNewUsersByCreatedAtYearMonth();
        return newUsers.stream().map(newUser ->
                new NewUsersCreatedAt(
                        (String) newUser[0],
                        (long) newUser[1])
        ).collect(Collectors.toList());
    }

    private UUID bytesToUUID(byte[] bytes) {
        return UUID.nameUUIDFromBytes(bytes);
    }
}