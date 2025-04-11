package ru.tbank.bookit.book_it_backend.service;

import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.repository.AreaRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AreaService {
    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository, BookingService bookingService) {
        this.areaRepository = areaRepository;
    }

    public List<Area> findAll() {
        return areaRepository.findAll();
    }

    public String findAreaNameById(UUID areaId) {
        Area area = areaRepository.findById(areaId)
                                  .orElseThrow(() -> new NoSuchElementException("Area with Id=" + areaId +" not found"));
        return area.getName();
    }
}
