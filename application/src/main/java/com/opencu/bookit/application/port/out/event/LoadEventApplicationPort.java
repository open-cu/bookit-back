package com.opencu.bookit.application.port.out.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencu.bookit.domain.model.event.EventApplicationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadEventApplicationPort {
    Optional<EventApplicationModel> findById(UUID id);
    Page<EventApplicationModel> findWithFilters(EventApplicationFilter filter, Pageable pageable);
    List<EventApplicationModel> findByUserId(UUID userId);

    record EventApplicationFilter(UUID userId, UUID eventId,
                                  LocalDate birthDateFromInclusive,
                                  LocalDate birthDateToInclusive,
                                  String cityOfResidence,
                                  JsonNode detailsFilter) {}
}