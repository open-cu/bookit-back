package com.opencu.bookit.adapter.in.web.controller;

import com.opencu.bookit.adapter.in.web.dto.response.AreaResponse;
import com.opencu.bookit.adapter.in.web.exception.ResourceNotFoundException;
import com.opencu.bookit.adapter.in.web.mapper.AreaResponseMapper;
import com.opencu.bookit.application.service.area.AreaService;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/areas")
@Deprecated
public class AreaController {
    private final AreaService areaService;
    private final AreaResponseMapper areaResponseMapper;

    public AreaController(AreaService areaService, AreaResponseMapper areaResponseMapper) {
        this.areaService = areaService;
        this.areaResponseMapper = areaResponseMapper;
    }

    @Operation(summary = "Get all areas")
    @GetMapping
    public List<AreaResponse> getAllAreas(
            @RequestParam(defaultValue = "true") Boolean sendPhotos
    ) {
        return areaService.findAll()
                .stream()
                .map(area -> {
                    try {
                        return areaResponseMapper.toAreaResponse(area, sendPhotos);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    @Operation(summary = "Get area by ID")
    @GetMapping("/{areaId}")
    public ResponseEntity<AreaResponse> getAreaById(
            @RequestParam(defaultValue = "true") Boolean sendPhotos,
            @PathVariable UUID areaId
    ) {
        Optional<AreaModel> area = areaService.findById(areaId);

        AreaResponse areaResponse = area
                .map(area1 -> {
                    try {
                        return areaResponseMapper.toAreaResponse(area1, sendPhotos);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Area not found with ID: " + areaId));
        return ResponseEntity.ok(areaResponse);
    }

    @GetMapping("/by-type")
    @Operation(summary = "Get areas by type")
    public List<AreaResponse> getAreasByType(
            @RequestParam(defaultValue = "true") Boolean sendPhotos,
            @RequestParam AreaType type
    ) {
        return areaService.findByType(type)
                          .stream()
                          .map(area -> {
                              try {
                                  return areaResponseMapper.toAreaResponse(area, sendPhotos);
                              } catch (IOException e) {
                                  throw new RuntimeException(e);
                              }
                          })
                          .toList();
    }

    @Operation(summary = "Get all area names")
    @GetMapping("/names")
    public List<String> getAllAreaNames() {
        return areaService.findAllAreaNames();
    }
}