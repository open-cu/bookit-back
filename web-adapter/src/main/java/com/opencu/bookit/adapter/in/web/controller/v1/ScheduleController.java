package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.application.service.schedule.ScheduleService;
import com.opencu.bookit.domain.model.schedule.WorkingDayConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/schedule")
@Tag(name = "Schedule Management", description = "Working schedule configuration and information")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/working-day-config/{date}")
    @Operation(
        summary = "Get working day configuration",
        description = "Returns working hours, booking interval, and holiday status for the specified date"
    )
    @ApiResponse(responseCode = "200", description = "Working day information retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid date format")
    public ResponseEntity<WorkingDayConfig> getWorkingDayConfig(
            @Parameter(description = "Date in YYYY-MM-DD format", example = "2025-09-09")
            @PathVariable 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            LocalDate date) {
        
        WorkingDayConfig config = scheduleService.getWorkingDayConfig(date);
        return ResponseEntity.ok(config);
    }
}