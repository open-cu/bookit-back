package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.repository.BookingStatsRepository;
import com.opencu.bookit.application.port.out.statstics.LoadBookingStatsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingStatsAdapter implements LoadBookingStatsPort {
    BookingStatsRepository bookingStatsRepository;

    @Override
    public List<Object[]> findBookingStatsBetweenDates(LocalDateTime localDateTime, LocalDateTime localDateTime1) {
        return bookingStatsRepository.findBookingStatsBetweenDates(localDateTime, localDateTime1);
    }
}
