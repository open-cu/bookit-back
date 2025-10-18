package com.opencu.bookit.adapter.in.web.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

@Schema(description = "Request to create a new event application")
public record CreateEventApplicationRequest(
        @Schema(description = "City of residence", example = "Moscow")
        @NotBlank(message = "City of residence cannot be blank")
        String cityOfResidence,

        @Schema(description = "Date of birth", example = "2005-01-15")
        @NotNull(message = "Date of birth cannot be null")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Schema(description = "Additional details about activity in JSON format")
        @NotNull(message = "Activity details cannot be null")
        JsonNode activityDetails
) {
}

