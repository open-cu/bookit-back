package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.entity.EventEntity;
import com.opencu.bookit.adapter.out.persistence.entity.UserEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.EventMapper;
import com.opencu.bookit.adapter.out.persistence.repository.EventRepository;
import com.opencu.bookit.adapter.out.persistence.repository.UserRepository;
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
        Specification<EventEntity> spec = Specification.where(null);

        if (startDate != null && endDate != null) {
            spec = spec.and((root, query, cb) ->
                cb.between(root.get("startTime"),
                        LocalDateTime.of(startDate, LocalTime.of(0,0,0)),
                        LocalDateTime.of(endDate, LocalTime.of(0,0,0))
            ));
        }
        if (tags != null && !tags.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.join("tags").in(tags));
        }
        if (formats != null && !formats.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.join("formats").in(formats));
        }
        if (times != null && !times.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.join("times").in(times));
        }
        if (participationFormats != null && !participationFormats.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.join("participationFormats").in(participationFormats));
        }

        if (targetAudiences != null && !targetAudiences.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.join("targetAudiences").in(targetAudiences));
        }
        if (search != null && !search.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
                    )
            );
        }

        if (status != null && currentUserId != null) {
            Optional<UserEntity> userOpt = userRepository.findById(currentUserId);
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                if (status.equalsIgnoreCase("registered")) {
                    spec = spec.and((root, query, cb) ->
                            cb.isMember(user, root.get("users")));
                } else if (status.equalsIgnoreCase("available")) {
                    spec = spec.and((root, query, cb) -> cb.and(
                            cb.greaterThan(root.get("available_places"), 0),
                            cb.not(cb.isMember(user, root.get("users")))
                    ));
                }
            }
        }

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

