package com.opencu.bookit.application.area.port.out;

import com.opencu.bookit.domain.model.area.Area;
import com.opencu.bookit.domain.model.area.AreaType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadAreaPort {
    List<Area> findByType(AreaType type);
    Optional<Area> findById(UUID areaId);
    List<Area> findAll();
}
