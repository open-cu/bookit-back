package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.user.UserStatus;

public record PatchUserRequest(
        String firstName,
        String lastName,
        String email,
        UserStatus userStatus
) {}
