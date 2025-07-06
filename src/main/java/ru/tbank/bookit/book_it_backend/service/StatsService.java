package ru.tbank.bookit.book_it_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.repository.BookingStatsRepository;
import ru.tbank.bookit.book_it_backend.DTO.BookingStatsResponse;

import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
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
                        ((Number) result[2]).longValue()
                ))
                .collect(Collectors.toList());
    }
}