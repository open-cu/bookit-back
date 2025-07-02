package com.opencu.bookit.application.area.service;

import com.opencu.bookit.application.booking.service.BookingService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

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

    public List<Area> findByType(AreaType type) {
        return areaRepository.findByType(type);
    }

    public List<String> findAllAreaNames() {
        return areaRepository.findAll().stream().map(Area::getName).toList();
    }
    public Optional<Area> findById(UUID areaId) {
        return areaRepository.findById(areaId);
    }
}
