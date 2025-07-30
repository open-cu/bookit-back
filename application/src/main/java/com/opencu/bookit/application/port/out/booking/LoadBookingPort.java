package com.opencu.bookit.application.port.out.booking;

import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.booking.TimeTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadBookingPort {
    Optional<BookingModel> findById(UUID bookingId);
    List<BookingModel> loadBookingsByUser(UUID userId, TimeTag timeTag);
    List<BookingModel> findByAreaId(UUID areaId);
    List<BookingModel> findByStartDatetime(LocalDateTime date);
    List<BookingModel> findAllIncludingTime(LocalDateTime date);
    List<BookingModel> findByDateAndArea(LocalDate date, UUID areaId);
    List<BookingModel> findAll();

    Page<BookingModel> findWithFilters(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable,
            UUID areaId,
            UUID userId
    );
}
