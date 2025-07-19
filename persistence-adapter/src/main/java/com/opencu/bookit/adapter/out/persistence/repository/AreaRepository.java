package com.opencu.bookit.adapter.out.persistence.repository;

import com.opencu.bookit.adapter.out.persistence.entity.AreaEntity;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;

public interface AreaRepository extends JpaRepository<AreaEntity, UUID> {
    List<AreaEntity> findByType(AreaType type);

    Page<AreaEntity> findAll(Specification<AreaEntity> spec, Pageable pageable);
}
