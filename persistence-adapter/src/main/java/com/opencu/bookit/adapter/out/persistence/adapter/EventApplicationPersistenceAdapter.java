package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.entity.EventApplicationEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.EventApplicationMapper;
import com.opencu.bookit.adapter.out.persistence.repository.EventApplicationRepository;
import com.opencu.bookit.domain.model.event.EventApplicationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventApplicationPersistenceAdapter {

    private final EventApplicationRepository eventApplicationRepository;
    private final EventApplicationMapper eventApplicationMapper;


    public EventApplicationModel save(EventApplicationModel eventApplicationModel) {
        EventApplicationEntity entity = eventApplicationMapper.toEntity(eventApplicationModel);
        EventApplicationEntity savedEntity = eventApplicationRepository.save(entity);
        return eventApplicationMapper.toModel(savedEntity);
    }


    public Optional<EventApplicationModel> findById(UUID id) {
        return eventApplicationRepository.findById(id).map(eventApplicationMapper::toModel);
    }


    public List<EventApplicationModel> findAll() {
        return eventApplicationMapper.toModelList(eventApplicationRepository.findAll());
    }
}
