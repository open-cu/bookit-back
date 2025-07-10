package com.opencu.bookit.application.port.out.event;

import com.opencu.bookit.domain.model.event.EventModel;
import com.opencu.bookit.domain.model.event.ThemeTags;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LoadEventPort {
    List<EventModel> findAll();
    List<EventModel> findByTags(Set<ThemeTags> tags);
    Optional<EventModel> findById(UUID eventId);
}
