package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.response.AreaResponse;
import com.opencu.bookit.adapter.in.web.exception.ResourceNotFoundException;
import com.opencu.bookit.adapter.in.web.mapper.AreaResponseMapper;
import com.opencu.bookit.application.service.area.AreaService;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaType;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/areas")
public class AreaControllerV1 {
    private final AreaService areaService;
    private final AreaResponseMapper areaMapper;

    public AreaControllerV1(AreaService areaService, AreaResponseMapper areaMapper) {
        this.areaService = areaService;
        this.areaMapper = areaMapper;
    }

    @Operation(summary = "Get all areas with filters and pagination")
    @GetMapping
    public ResponseEntity<Page<AreaResponse>> getAllAreas(
            @RequestParam(required = false) String areaName,
            @RequestParam(required = false) AreaType type,
            @RequestParam(defaultValue = "${pagination.default-page}") int page,
            @RequestParam(defaultValue = "${pagination.default-size}") int size
    ) {
        Sort.Direction direction = Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "type"));
        Page<AreaResponse> areasPage = areaService
                .findWithFilters(areaName, type, pageable)
                .map(area -> {
                    try {
                        return areaMapper.toAreaResponse(area);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        return ResponseEntity.ok(areasPage);
    }

    @Operation(summary = "Get area by ID")
    @GetMapping("/{areaId}")
    public ResponseEntity<AreaResponse> getAreaById(@PathVariable UUID areaId) {
        Optional<AreaModel> area = areaService.findById(areaId);

        AreaResponse areaResponse = area
                .map(area1 -> {
                    try {
                        return areaMapper.toAreaResponse(area1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Area not found with ID: " + areaId));
        return ResponseEntity.ok(areaResponse);
    }
}