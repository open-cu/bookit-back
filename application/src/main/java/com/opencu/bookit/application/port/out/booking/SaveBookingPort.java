package com.opencu.bookit.application.port.out.booking;

import com.opencu.bookit.domain.model.booking.BookingModel;

import java.util.Set;

public interface SaveBookingPort {
    BookingModel save(BookingModel bookingModel);
    void saveAll(Set<BookingModel> result);
}