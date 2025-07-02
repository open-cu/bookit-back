package com.opencu.bookit.application.event.port.out;

import com.opencu.bookit.domain.model.event.Event;
import com.opencu.bookit.domain.model.event.ThemeTags;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LoadEventPort {
    List<Event> findAll();
    List<Event> findByTags(Set<ThemeTags> tags);
    Optional<Event> findById(UUID eventId);
}
