package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.CreateAreaRequest;
import com.opencu.bookit.adapter.in.web.dto.request.UpdateAreaRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/areas")
public class AdminAreaControllerV1 {
    private final AreaService areaService;
    private final AreaResponseMapper areaMapper;

    public AdminAreaControllerV1(AreaService areaService, AreaResponseMapper areaMapper) {
        this.areaService = areaService;
        this.areaMapper = areaMapper;
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @Operation(summary = "Get all areas with filters and pagination")
    @GetMapping
    public ResponseEntity<Page<AreaResponse>> getAllAreas(
            @RequestParam(required = false) AreaType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Sort.Direction direction = Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "type"));
        Page<AreaResponse> areasPage = areaService
                .findWithFilters(type, pageable)
                .map(areaMapper::toAreaResponse);
        return ResponseEntity.ok(areasPage);
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @Operation(summary = "Get area by ID")
    @GetMapping("/{areaId}")
    public ResponseEntity<AreaResponse> getAreaById(@PathVariable UUID areaId) {
        Optional<AreaModel> area = areaService.findById(areaId);

        AreaResponse areaResponse = area
                .map(areaMapper::toAreaResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found with ID: " + areaId));
        return ResponseEntity.ok(areaResponse);
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @Operation(summary = "Create area")
    @PostMapping
    public ResponseEntity<AreaResponse> createArea(
            @RequestBody CreateAreaRequest createAreaRequest
    ) {
        AreaModel model = areaService.createArea(
                createAreaRequest.name(),
                createAreaRequest.description(),
                createAreaRequest.type(),
                createAreaRequest.features(),
                createAreaRequest.capacity(),
                createAreaRequest.status()
        );
        return ResponseEntity.ok(
                areaMapper.toAreaResponse(model)
        );
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @Operation(summary = "Delete area by id")
    @DeleteMapping("/{areaId}")
    public ResponseEntity<?> deleteById(
            @PathVariable UUID areaId
    ) {
        areaService.deleteById(areaId);
        return ResponseEntity.ok("Area successfully deleted");
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @Operation(summary = "Updated area information")
    @PutMapping("/{areaId}")
    public ResponseEntity<AreaResponse> updateById(
            @PathVariable UUID areaId,
            @RequestBody UpdateAreaRequest updateAreaRequest
    ) {
        try {
            AreaResponse response = areaMapper.toAreaResponse(
                    areaService.updateById(
                            areaId,
                            updateAreaRequest.name(),
                            updateAreaRequest.type(),
                            updateAreaRequest.capacity()
                    )
            );
            return ResponseEntity.ok(response);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
