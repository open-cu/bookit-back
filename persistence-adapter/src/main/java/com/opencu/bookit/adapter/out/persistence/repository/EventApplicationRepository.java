package com.opencu.bookit.adapter.out.persistence.repository;

import com.opencu.bookit.adapter.out.persistence.entity.EventApplicationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventApplicationRepository extends JpaRepository<EventApplicationEntity, UUID> {
    Page<EventApplicationEntity> findAll(Specification<EventApplicationEntity> spec, Pageable pageable);
    List<EventApplicationEntity> findByUserId(UUID userId);
}
