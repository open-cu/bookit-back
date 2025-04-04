package ru.tbank.bookit.book_it_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.repository.AreaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AreaService {
    private final AreaRepository areaRepository;
    private final BookingService bookingService;

    public AreaService(AreaRepository areaRepository, BookingService bookingService) {
        this.areaRepository = areaRepository;
        this.bookingService = bookingService;
    }

    public List<String> findAvailableArea(LocalDateTime time) {
        List<String> availableAreas = areaRepository.findAll().stream()
                                                    .map(b -> Long.toString(b.getId()))
                                                    .toList();
        List<Booking> bookings = bookingService.findByDatetime(time);

        for (Booking b : bookings) {
            availableAreas.remove(b.getAreaId());
        }
        return availableAreas;
    }
}
