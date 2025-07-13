package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.area.Review;
import com.opencu.bookit.domain.model.user.Role;
import com.opencu.bookit.domain.model.user.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record MeResponse(
        UUID id,
        Long tgId,
        String firstName,
        String lastName,
        String photoUrl,
        String email,
        String phone,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String username,
        Set<Role> roles
) {}