package com.opencu.bookit.application.port.out.booking;

import java.util.UUID;

public interface DeleteBookingPort {
    void deleteById(UUID bookingId);
}
