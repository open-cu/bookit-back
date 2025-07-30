package com.opencu.bookit.application.port.out.event;

import com.opencu.bookit.domain.model.contentcategory.ContentFormat;
import com.opencu.bookit.domain.model.contentcategory.ContentTime;
import com.opencu.bookit.domain.model.contentcategory.ParticipationFormat;
import com.opencu.bookit.domain.model.event.EventModel;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LoadEventPort {
    List<EventModel> findAll();
    List<EventModel> findByTags(Set<ThemeTags> tags);
    Optional<EventModel> findById(UUID eventId);
    Page<EventModel> findWithFilters(
            LocalDate startDate,
            LocalDate endDate,
            Set<ThemeTags> tags,
            Set<ContentFormat> formats,
            Set<ContentTime> times,
            Set<ParticipationFormat> participationFormats,
            String search,
            String status,
            Pageable pageable,
            UUID currentUserId
    );
    boolean existsById(UUID eventId);
    Optional<EventModel> findByName(String name);
}
