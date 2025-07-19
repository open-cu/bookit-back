package com.opencu.bookit.application.service.area;

import com.opencu.bookit.application.port.out.area.LoadAreaPort;
import com.opencu.bookit.application.service.booking.BookingService;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaStatus;
import com.opencu.bookit.domain.model.area.AreaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class AreaService {
    private final LoadAreaPort loadAreaPort;

    public AreaService(LoadAreaPort loadAreaPort, BookingService bookingService) {
        this.loadAreaPort = loadAreaPort;
    }

    public List<AreaModel> findAll() {
        return loadAreaPort.findAll();
    }

    public String findAreaNameById(UUID areaId) {
        AreaModel areaModel = loadAreaPort.findById(areaId)
                                          .orElseThrow(() -> new NoSuchElementException("Area with Id=" + areaId +" not found"));
        return areaModel.getName();
    }

    public List<AreaModel> findByType(AreaType type) {
        return loadAreaPort.findByType(type);
    }

    public List<String> findAllAreaNames() {
        return loadAreaPort.findAll().stream().map(AreaModel::getName).toList();
    }
    public Optional<AreaModel> findById(UUID areaId) {
        return loadAreaPort.findById(areaId);
    }

    public Page<AreaModel> findWithFilters(AreaType type, Pageable pageable) {
        return loadAreaPort.findWithFilters(type, pageable);
    }
}
