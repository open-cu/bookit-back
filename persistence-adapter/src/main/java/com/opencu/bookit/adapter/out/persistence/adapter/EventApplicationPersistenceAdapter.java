package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.entity.EventApplicationEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.EventApplicationMapper;
import com.opencu.bookit.adapter.out.persistence.repository.EventApplicationRepository;
import com.opencu.bookit.application.port.out.event.DeleteEventApplicationPort;
import com.opencu.bookit.application.port.out.event.LoadEventApplicationPort;
import com.opencu.bookit.application.port.out.event.SaveEventApplicationPort;
import com.opencu.bookit.domain.model.event.EventApplicationModel;
import com.opencu.bookit.domain.model.event.EventApplicationStatus;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventApplicationPersistenceAdapter implements LoadEventApplicationPort, SaveEventApplicationPort,
        DeleteEventApplicationPort {

    private final EventApplicationRepository eventApplicationRepository;
    private final EventApplicationMapper eventApplicationMapper;

    @Override
    public EventApplicationModel save(EventApplicationModel eventApplicationModel) {
        EventApplicationEntity entity = eventApplicationMapper.toEntity(eventApplicationModel);
        EventApplicationEntity savedEntity = eventApplicationRepository.save(entity);
        return eventApplicationMapper.toModel(savedEntity);
    }

    @Override
    public void changeStatusById(UUID id, EventApplicationStatus status) {
        eventApplicationRepository.findById(id).ifPresent(entity -> {
            entity.setStatus(status);
            eventApplicationRepository.save(entity);
        });
    }

    @Override
    public Optional<EventApplicationModel> findById(UUID id) {
        return eventApplicationRepository.findById(id).map(eventApplicationMapper::toModel);
    }

    @Override
    public Page<EventApplicationModel> findWithFilters(EventApplicationFilter filter, Pageable pageable) {
        Specification<EventApplicationEntity> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (filter.userId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("user").get("id"), filter.userId()));
            }

            if (filter.eventId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("event").get("id"), filter.eventId()));
            }

            if (filter.birthDateFromInclusive() != null && filter.birthDateToInclusive() != null) {
                predicate = cb.and(predicate, cb.between(root.get("dateOfBirth"), filter.birthDateFromInclusive(), filter.birthDateToInclusive()));
            } else if (filter.birthDateFromInclusive() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("dateOfBirth"), filter.birthDateFromInclusive()));
            } else if (filter.birthDateToInclusive() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dateOfBirth"), filter.birthDateToInclusive()));
            }

            if (filter.cityOfResidence() != null && !filter.cityOfResidence().isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("cityOfResidence")), "%" + filter.cityOfResidence().toLowerCase() + "%"));
            }

            if (filter.detailsFilter() != null && !filter.detailsFilter().isNull()) {
                predicate = cb.and(predicate, cb.isTrue(cb.function(
                        "jsonb_contains",
                        Boolean.class,
                        root.get("activityDetails"),
                        cb.literal(filter.detailsFilter().toString())
                )));
            }

            return predicate;
        };

        return eventApplicationRepository.findAll(spec, pageable).map(eventApplicationMapper::toModel);
    }

    @Override
    public List<EventApplicationModel> findByUserId(UUID userId) {
        return  eventApplicationRepository.findByUserId(userId).stream().map(eventApplicationMapper::toModel).toList();
    }

    @Override
    public void deleteById(UUID id) {
        eventApplicationRepository.deleteById(id);
    }
}
