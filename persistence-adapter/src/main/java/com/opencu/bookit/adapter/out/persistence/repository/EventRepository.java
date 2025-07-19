package com.opencu.bookit.adapter.out.persistence.repository;

import com.opencu.bookit.adapter.out.persistence.entity.EventEntity;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.event.EventModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID>, JpaSpecificationExecutor<EventEntity> {
    @Query("""
    SELECT DISTINCT e
    FROM EventEntity e 
    JOIN e.tags t 
    WHERE t IN :tags 
    ORDER BY e.date DESC
""")
    List<EventEntity> findByTagsIn(@Param("tags") Set<ThemeTags> tags);

    Page<EventEntity> findAll(Specification<EventEntity> spec, Pageable pageable);

    @Modifying
    @Query("DELETE FROM EventEntity e WHERE e.id = :eventId")
    void delete(@Param("eventId") UUID eventId);

    @Query("SELECT e FROM EventEntity e WHERE e.name = :name")
    Optional<EventModel> findByName(@Param("name") String name);
}