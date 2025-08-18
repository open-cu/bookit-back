package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.user.Role;
import com.opencu.bookit.domain.model.user.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record MeResponse(
        UUID id,
        Long tgId,
        String firstName,
        String lastName,
        @Schema(nullable = true)
        String photoUrl,
        @Schema(nullable = true)
        String email,
        @Schema(nullable = true)
        String phone,
        UserStatus status,
        LocalDateTime createdAt,
        @Schema(nullable = true)
        LocalDateTime updatedAt,
        @Schema(nullable = true)
        String username,
        Set<Role> roles
) {}