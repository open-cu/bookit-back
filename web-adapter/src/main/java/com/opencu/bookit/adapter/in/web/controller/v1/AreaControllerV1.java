package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.response.AreaResponse;
import com.opencu.bookit.adapter.in.web.exception.ResourceNotFoundException;
import com.opencu.bookit.adapter.in.web.mapper.AreaResponseMapper;
import com.opencu.bookit.application.service.area.AreaService;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaType;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        Optional<AreaModel> area = areaService.findById(areaId);

        AreaResponse areaResponse = area
                .map(areaMapper::toAreaResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found with ID: " + areaId));
        return ResponseEntity.ok(areaResponse);
    }
}