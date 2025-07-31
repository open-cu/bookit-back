package com.opencu.bookit.application.port.out.booking;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DeleteBookingPort {
    void deleteById(UUID bookingId);
    void deleteBookingAccordingToIndirectParameters(UUID userId, UUID areaId, LocalDateTime startTime, LocalDateTime endTime);
}
