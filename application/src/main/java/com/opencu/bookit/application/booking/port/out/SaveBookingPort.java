package com.opencu.bookit.application.booking.port.out;

import com.opencu.bookit.domain.model.booking.Booking;

import java.util.Set;

public interface SaveBookingPort {
    void saveAll(Booking booking);
    Booking save(Booking booking);
    void saveAll(Set<Booking> result);
}