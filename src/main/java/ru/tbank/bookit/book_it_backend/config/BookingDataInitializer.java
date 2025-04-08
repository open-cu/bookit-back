package ru.tbank.bookit.book_it_backend.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.BookingStatus;
import ru.tbank.bookit.book_it_backend.repository.BookingRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingDataInitializer {

    private final BookingRepository bookingRepository;

    @PostConstruct
    public void initBooking() {
        Booking booking1 = new Booking();
        booking1.setUserId("bc6a92b5-b217-4c01-9aa4-d013d84f92bb");
        booking1.setAreaId("839d5785-f474-4797-8dfa-542a231c85e9");
        booking1.setStartTime(LocalDateTime.of(LocalDate.of(2025, 4, 14), LocalTime.of(10, 0)));
        booking1.setEndTime(LocalDateTime.of(LocalDate.of(2025, 4, 14), LocalTime.of(12, 0)));
        booking1.setQuantity(1);
        booking1.setStatus(BookingStatus.CONFIRMED);
        booking1.setCreatedAt(LocalDateTime.of(2025, 4, 3, 22, 37, 35, 996095700));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setUserId("522bfc4c-49d7-400d-a2d4-d1916f79b4cb");
        booking2.setAreaId("cb97b957-9d79-4185-acd6-14b1551c6008");
        booking2.setStartTime(LocalDateTime.of(LocalDate.of(2025, 4, 14), LocalTime.of(10, 0)));
        booking2.setEndTime(LocalDateTime.of(LocalDate.of(2025, 4, 14), LocalTime.of(12, 0)));
        booking2.setQuantity(1);
        booking2.setStatus(BookingStatus.CONFIRMED);
        booking2.setCreatedAt(LocalDateTime.of(2025, 4, 3, 22, 39, 25, 746173300));
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setUserId("baad0a41-1274-43d0-915b-a5f31e0b3e12");
        booking3.setAreaId("cb97b957-9d79-4185-acd6-14b1551c6008");
        booking3.setStartTime(LocalDateTime.of(LocalDate.of(2025, 4, 14), LocalTime.of(10, 0)));
        booking3.setEndTime(LocalDateTime.of(LocalDate.of(2025, 4, 14), LocalTime.of(12, 0)));
        booking3.setQuantity(1);
        booking3.setStatus(BookingStatus.CONFIRMED);
        booking3.setCreatedAt(LocalDateTime.of(2025, 4, 3, 22, 39, 25, 746173300));
        bookingRepository.save(booking3);
    }
}
