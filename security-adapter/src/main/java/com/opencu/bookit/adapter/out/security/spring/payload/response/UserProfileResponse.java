package com.opencu.bookit.adapter.out.security.spring.payload.response;

import com.opencu.bookit.domain.model.user.UserStatus;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String email,
        String phone,
        Long tgId,
        String photoUrl,
        UserStatus status
) {}