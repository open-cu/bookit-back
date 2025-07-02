package com.opencu.bookit.application.area.service;

import com.opencu.bookit.application.area.port.out.LoadAreaPort;
import com.opencu.bookit.application.booking.service.BookingService;
import com.opencu.bookit.domain.model.area.Area;
import com.opencu.bookit.domain.model.area.AreaType;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class AreaService {
    private final LoadAreaPort loadAreaPort;

    public AreaService(LoadAreaPort loadAreaPort, BookingService bookingService) {
        this.loadAreaPort = loadAreaPort;
    }

    public List<Area> findAll() {
        return loadAreaPort.findAll();
    }

    public String findAreaNameById(UUID areaId) {
        Area area = loadAreaPort.findById(areaId)
                                .orElseThrow(() -> new NoSuchElementException("Area with Id=" + areaId +" not found"));
        return area.getName();
    }

    public List<Area> findByType(AreaType type) {
        return loadAreaPort.findByType(type);
    }

    public List<String> findAllAreaNames() {
        return loadAreaPort.findAll().stream().map(Area::getName).toList();
    }
    public Optional<Area> findById(UUID areaId) {
        return loadAreaPort.findById(areaId);
    }
}
