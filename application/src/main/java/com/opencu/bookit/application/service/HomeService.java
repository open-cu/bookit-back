package com.opencu.bookit.application.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.opencu.bookit.application.area.service.AreaService;
import com.opencu.bookit.application.booking.service.BookingService;
import com.opencu.bookit.application.user.service.UserService;
import com.opencu.bookit.domain.model.booking.Booking;
import com.opencu.bookit.domain.model.user.User;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class HomeService {
    private final BookingService bookingService;
    private final UserService userService;
    private final AreaService areaService;

    public HomeService(BookingService bookingService, UserService userService,
                       AreaService areaService) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.areaService = areaService;
    }

    public List<Booking> getCurrentBookings(UUID userId) {
        return bookingService.getCurrentBookings(userId);
    }

    public List<Booking> getFutureBookings(UUID userId) {
        return bookingService.getFutureBookings(userId);
    }

    public List<Booking> getPastBookings(UUID userId) {
        return bookingService.getPastBookings(userId);
    }

    public void cancelBooking(UUID bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    public Optional<User> findUserById(UUID userId) {
        return userService.findById(userId);
    }

    public UUID getTestUserId() {
        return userService.getTestUserId();
    }

    public String findAreaNameById(UUID areaId) {
        return areaService.findAreaNameById(areaId);
    }
}