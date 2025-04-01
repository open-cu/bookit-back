package ru.tbank.bookit.book_it_backend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.User;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

@Service
public class HomepageService {
    private final BookingService bookingService;

    @Autowired
    public HomepageService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public String generateUserQrCode(User user) {
        String userData = String.format(
                "USER:%d:%s:%s",
                user.getId(),
                user.getName(),
                user.getTg_id()
        );

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    userData,
                    BarcodeFormat.QR_CODE,
                    200, 200
            );

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    public List<Booking> getCurrentBookings() {
        return bookingService.getCurrentBookings();
    }

    public List<Booking> getFutureBookings() {
        return bookingService.getFutureBookings();
    }

    public List<Booking> getPastBookings() {
        return bookingService.getPastBookings();
    }

    public void cancelBooking(long bookingId) {
        bookingService.cancelBooking(bookingId);
    }
}