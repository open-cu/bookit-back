package com.opencu.bookit.application.port.out.area;

import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadAreaPort {
    List<AreaModel> findByType(AreaType type);
    Optional<AreaModel> findById(UUID areaId);
    List<AreaModel> findAll();
}
