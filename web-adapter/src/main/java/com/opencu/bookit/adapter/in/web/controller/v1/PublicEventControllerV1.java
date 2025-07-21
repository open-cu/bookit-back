package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.response.EventResponse;
import com.opencu.bookit.adapter.in.web.mapper.EventResponseMapper;
import com.opencu.bookit.application.service.event.EventService;
import com.opencu.bookit.domain.model.contentcategory.ContentFormat;
import com.opencu.bookit.domain.model.contentcategory.ContentTime;
import com.opencu.bookit.domain.model.contentcategory.ParticipationFormat;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/public/events")
public class PublicEventControllerV1 {
    private final EventService eventService;
    private final EventResponseMapper eventResponseMapper;

    public PublicEventControllerV1(EventService eventService, EventResponseMapper eventResponseMapper) {
        this.eventService = eventService;
        this.eventResponseMapper = eventResponseMapper;
    }

    @Operation(summary = "Get all public events with optional filters")
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllPublicEvents(
            @RequestParam(required = false) Set<ThemeTags> tags,
            @RequestParam(required = false) Set<ContentFormat> formats,
            @RequestParam(required = false) Set<ContentTime> times,
            @RequestParam(required = false) Set<ParticipationFormat> participationFormats,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "${pagination.default-page}") int page,
            @RequestParam(defaultValue = "${pagination.default-size}") int size,
            @RequestParam(defaultValue = "date,asc") String sort
                                                                 ) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortBy = sortParams[0];
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<EventResponse> events = eventService.findWithFilters(
                tags, formats, times, participationFormats,
                search, null, pageable, null
        ).map(event -> {
            try {
                return eventResponseMapper.toEventResponse(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return ResponseEntity.ok(events);
    }
}