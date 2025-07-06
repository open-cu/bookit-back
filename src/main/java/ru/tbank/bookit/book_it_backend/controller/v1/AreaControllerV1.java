package ru.tbank.bookit.book_it_backend.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.DTO.AreaResponse;
import ru.tbank.bookit.book_it_backend.exception.ResourceNotFoundException;
import ru.tbank.bookit.book_it_backend.mapper.AreaMapper;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.model.AreaType;
import ru.tbank.bookit.book_it_backend.service.AreaService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/areas")
public class AreaControllerV1 {
    private final AreaService areaService;
    private final AreaMapper areaMapper;

    public AreaControllerV1(AreaService areaService, AreaMapper areaMapper) {
        this.areaService = areaService;
        this.areaMapper = areaMapper;
    }

    @Operation(summary = "Get all areas")
    @GetMapping
    public List<AreaResponse> getAllAreas(@RequestParam(required = false) AreaType type) {
        if (type != null) {
            return areaService.findByType(type)
                    .stream()
                    .map(areaMapper::toAreaResponse)
                    .toList();
        }
        return areaService.findAll()
                .stream()
                .map(areaMapper::toAreaResponse)
                .toList();
    }

    @Operation(summary = "Get area by ID")
    @GetMapping("/{areaId}")
    public ResponseEntity<AreaResponse> getAreaById(@PathVariable UUID areaId) {
        Optional<Area> area = areaService.findById(areaId);

        AreaResponse areaResponse = area
                .map(areaMapper::toAreaResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found with ID: " + areaId));
        return ResponseEntity.ok(areaResponse);
    }
}