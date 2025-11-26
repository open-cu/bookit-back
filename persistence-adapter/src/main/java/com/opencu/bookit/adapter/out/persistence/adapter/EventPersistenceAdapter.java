package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.entity.EventEntity;
import com.opencu.bookit.adapter.out.persistence.entity.UserEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.EventMapper;
import com.opencu.bookit.adapter.out.persistence.repository.EventRepository;
import com.opencu.bookit.adapter.out.persistence.repository.UserRepository;
import com.opencu.bookit.adapter.out.persistence.specifications.EventSpecifications;
import com.opencu.bookit.application.port.out.event.DeleteEventPort;
import com.opencu.bookit.application.port.out.event.LoadEventPort;
import com.opencu.bookit.application.port.out.event.SaveEventPort;
import com.opencu.bookit.domain.model.contentcategory.*;
import com.opencu.bookit.domain.model.event.EventModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventPersistenceAdapter implements LoadEventPort,
        SaveEventPort, DeleteEventPort {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
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
    public Page<EventModel> findWithFilters(
            LocalDate startDate,
            LocalDate endDate,
            Set<ThemeTags> tags,
            Set<ContentFormat> formats,
            Set<ContentTime> times,
            Set<ParticipationFormat> participationFormats,
            Set<TargetAudience> targetAudiences,
            String search,
            String status,
            Pageable pageable,
            UUID currentUserId
    ) {
        Specification<EventEntity> spec = Specification
            .where(EventSpecifications.startBetweenInclusive(startDate, endDate))
            .and(EventSpecifications.hasAnyTags(tags))
            .and(EventSpecifications.hasAnyFormats(formats))
            .and(EventSpecifications.hasAnyTimes(times))
            .and(EventSpecifications.hasAnyParticipationFormats(participationFormats))
            .and(EventSpecifications.hasAnyTargetAudiences(targetAudiences))
            .and(EventSpecifications.search(search))
            .and(EventSpecifications.withStatus(status, currentUserId));
        return eventRepository.findAll(spec, pageable).map(eventMapper::toModel);
    }

    @Override
    public Optional<EventModel> findByName(String name) {
        return eventRepository.findByName(name)
                .map(eventMapper::toModel);
    }

    @Override
    public boolean existsById(UUID eventId) {
        return eventRepository.existsById(eventId);
    }

    @Override
    public boolean requiresApplication(UUID eventId) {
        return eventRepository.requiresApplication(eventId);
    }

    @Override
    public EventModel save(EventModel eventModel) {
        EventEntity entity = eventMapper.toEntity(eventModel);
        EventEntity savedEntity = eventRepository.save(eventMapper.toEntity(eventModel));
        return eventMapper.toModel(savedEntity);
    }

    @Override
    public void delete(UUID eventId) {
        eventRepository.delete(eventId);
    }
}

