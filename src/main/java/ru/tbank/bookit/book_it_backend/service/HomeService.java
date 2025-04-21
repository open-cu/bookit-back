package ru.tbank.bookit.book_it_backend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.repository.AreaRepository;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class HomeService {
    private final BookingService bookingService;
    private final UserService userService;
    private final AreaService areaService;

    public HomeService(BookingService bookingService, UserService userService, AreaRepository areaRepository,
                       AreaService areaService) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.areaService = areaService;
    }

    public String generateUserQrCode(User user) {
        String userData = String.format(
                "USER:%s:%s:%s",
                user.getId(),
                user.getName(),
                user.getTg_id());

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 0);

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    userData,
                    BarcodeFormat.QR_CODE,
                    200, 200,
                    hints);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
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