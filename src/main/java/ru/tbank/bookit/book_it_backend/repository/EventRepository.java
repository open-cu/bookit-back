package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID>, JpaSpecificationExecutor<Event> {
    @Query("""
    SELECT DISTINCT e 
    FROM Event e 
    JOIN e.tags t 
    WHERE t IN :tags 
    ORDER BY e.date DESC
""")
    List<Event> findByTagsIn(@Param("tags") Set<ThemeTags> tags);

    Page<Event> findAll(Specification<Event> spec, Pageable pageable);
}