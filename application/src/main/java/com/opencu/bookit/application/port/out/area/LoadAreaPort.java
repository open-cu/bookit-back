package com.opencu.bookit.application.port.out.area;

import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaStatus;
import com.opencu.bookit.domain.model.area.AreaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LoadAreaPort {
    List<AreaModel> findByType(AreaType type);
    Optional<AreaModel> findById(UUID areaId);
    List<AreaModel> findAll();

    Page<AreaModel> findWithFilters(
            AreaType type,
            AreaStatus status,
            Pageable pageable);
}
