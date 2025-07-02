package com.opencu.bookit.application.booking.port.out;

import com.opencu.bookit.domain.model.booking.Booking;
import com.opencu.bookit.domain.model.booking.TimeTag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadBookingPort {
    Optional<Booking> findById(UUID bookingId);
    List<Booking> loadBookingsByUser(UUID userId, TimeTag timeTag);
    List<Booking> findByAreaId(UUID areaId);
    List<Booking> findByStartDatetime(LocalDateTime date);
    List<Booking> findByDatetime(LocalDateTime date);
    List<Booking> findByDateAndArea(LocalDate date, UUID areaId);
    List<Booking> findAll();
}
