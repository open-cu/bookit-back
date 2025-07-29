package com.opencu.bookit.application.service.area;

import com.opencu.bookit.application.port.out.area.DeleteAreaPort;
import com.opencu.bookit.application.port.out.area.LoadAreaPort;
import com.opencu.bookit.application.port.out.area.SaveAreaPort;
import com.opencu.bookit.application.service.booking.BookingService;
import com.opencu.bookit.domain.model.area.AreaFeature;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaStatus;
import com.opencu.bookit.domain.model.area.AreaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class AreaService {
    private final LoadAreaPort loadAreaPort;
    private final SaveAreaPort saveAreaPort;
    private final DeleteAreaPort deleteAreaPort;

    public AreaService(LoadAreaPort loadAreaPort, BookingService bookingService, SaveAreaPort saveAreaPort, DeleteAreaPort deleteAreaPort) {
        this.loadAreaPort = loadAreaPort;
        this.saveAreaPort = saveAreaPort;
        this.deleteAreaPort = deleteAreaPort;
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

    public Page<AreaModel> findWithFilters(String areaName, AreaType type, Pageable pageable) {
        return loadAreaPort.findWithFilters(areaName, type, pageable);
    }

    @Transactional
    public AreaModel createArea(
            String name,
            String description,
            AreaType type,
            List<AreaFeature> features,
            List<String> keys,
            int capacity,
            AreaStatus status
    ) {
        AreaModel model = new AreaModel();
        model.setName(name);
        model.setDescription(description);
        model.setType(type);
        model.setFeatures(features.getFirst());
        model.setCapacity(capacity);
        model.setKeys(keys);
        model.setStatus(status);

        return saveAreaPort.save(model);
    }

    @Transactional
    public void deleteById(UUID areaId) {
        deleteAreaPort.deleteById(areaId);
    }

    @Transactional
    public AreaModel updateById(
            UUID areaId,
            String name,
            AreaType type,
            List<String> keys,
            int capacity
    ) {
        Optional<AreaModel> areaOpt = loadAreaPort.findById(areaId);
        if (areaOpt.isEmpty()) {
            throw new NoSuchElementException();
        }
        AreaModel model = areaOpt.get();
        model.setName(name);
        model.setType(type);
        model.setKeys(keys);
        model.setCapacity(capacity);
        return saveAreaPort.save(model);
    }
}
