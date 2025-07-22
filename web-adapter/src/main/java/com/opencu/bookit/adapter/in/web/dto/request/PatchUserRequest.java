package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.user.UserStatus;

import java.util.List;

public record PatchUserRequest(
        String firstName,
        String lastName,
        String email,
        List<String> roles,
        UserStatus userStatus
) {}
