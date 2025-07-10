package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.mapper.EventMapper;
import com.opencu.bookit.adapter.out.persistence.repository.EventRepository;
import com.opencu.bookit.application.port.out.event.LoadEventPort;
import com.opencu.bookit.application.port.out.event.SaveEventPort;
import com.opencu.bookit.domain.model.event.EventModel;
import com.opencu.bookit.domain.model.event.ThemeTags;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventPersistenceAdapter implements LoadEventPort, SaveEventPort {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventModel> findAll() {
        return eventMapper.toModelList(eventRepository.findAll());
    }

    @Override
    public List<EventModel> findByTags(Set<ThemeTags> tags) {
        return eventMapper.toModelList(eventRepository.findByTagsIn(tags));
    }

    @Override
    public Optional<EventModel> findById(UUID eventId) {
        return eventRepository.findById(eventId).map(eventMapper::toModel);
    }

    @Override
    public void save(EventModel eventModel) {
        eventRepository.save(eventMapper.toEntity(eventModel));
    }
}

