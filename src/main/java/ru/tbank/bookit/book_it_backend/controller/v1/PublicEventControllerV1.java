package ru.tbank.bookit.book_it_backend.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.DTO.EventResponse;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.service.EventService;

import java.util.List;
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
    public ResponseEntity<List<EventResponse>> getAllPublicEvents(
            @RequestParam(required = false) Set<ThemeTags> tags,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate,asc") String sort
    ) {
        // TODO: реализовать фильтрацию, поиск, сортировку и пагинацию
        if (tags != null && !tags.isEmpty()) {
            return ResponseEntity.ok(eventService.findByTags(tags));
        } else {
            return ResponseEntity.ok(eventService.findAll());
        }
    }
}