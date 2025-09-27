package com.opencu.bookit.application.port.in.booking;

import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface CRUDBookingUseCase {
    UUID createBooking(CreateBookingCommand command);
    record CreateBookingCommand(
            UUID userId,
            UUID areaId,
            UUID eventId,
            Set<Pair<LocalDateTime, LocalDateTime>> timePeriods,
            int quantity) {}

    void updateBooking(UUID bookingId, UpdateBookingQuery query);
    record UpdateBookingQuery(
            UUID areaId,
            LocalDateTime startTime,
            LocalDateTime endTime) {}
}