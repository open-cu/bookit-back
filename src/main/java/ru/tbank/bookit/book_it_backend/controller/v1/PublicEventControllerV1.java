package ru.tbank.bookit.book_it_backend.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.DTO.EventResponse;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.service.EventService;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/public/events")
public class PublicEventControllerV1 {
    private final EventService eventService;

    public PublicEventControllerV1(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Get all public events with optional filters")
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllPublicEvents(
            @RequestParam(required = false) Set<ThemeTags> tags,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date,asc") String sort
    ) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortBy = sortParams[0];
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<EventResponse> events = eventService.findWithFilters(tags, search, null, pageable, null);

        return ResponseEntity.ok(events);
    }
}