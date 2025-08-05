package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.repository.BookingStatsRepository;
import com.opencu.bookit.application.port.out.statstics.LoadBookingStatsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingStatsAdapter implements LoadBookingStatsPort {
    BookingStatsRepository bookingStatsRepository;

    @Autowired
    public BookingStatsAdapter(BookingStatsRepository bookingStatsRepository) {
        this.bookingStatsRepository = bookingStatsRepository;
    }

    @Override
    public List<Object[]> findBookingStatsBetweenDates(
            LocalDateTime start,
            LocalDateTime end)
    {
        return bookingStatsRepository.findBookingStatsBetweenDates(start, end);
    }

    @Override
    public List<Object[]> findBookingStatsByDayOfWeek(LocalDateTime start, LocalDateTime end) {
        return bookingStatsRepository.findBookingStatsByDayOfWeek(start, end);
    }

    @Override
    public List<Object[]> findCancellationStatsByArea(LocalDateTime start, LocalDateTime end) {
        return bookingStatsRepository.findCancellationStatsByArea(start, end);
    }

    @Override
    public List<Object[]> findBusiestHours(LocalDateTime start, LocalDateTime end, String areaName) {
        return bookingStatsRepository.findBusiestHours(start, end, areaName);
    }

    @Override
    public List<Object[]> findEventOverlapPercentage() {
        return bookingStatsRepository.findEventOverlapPercentage();
    }

    @Override
    public List<Object[]> findEventOverlapPercentage(UUID eventId1, UUID eventId2) {
        return bookingStatsRepository.findEventOverlapPercentage(eventId1, eventId2);
    }

    @Override
    public List<Object[]> findEventOverlapPercentage(UUID eventId) {
        return bookingStatsRepository.findEventOverlapPercentage(eventId);
    }

    @Override
    public List<Object[]> findNewUsersByCreatedAtYearMonth() {
        return bookingStatsRepository.findNewUsersByCreatedAtYearMonth();
    }
}
