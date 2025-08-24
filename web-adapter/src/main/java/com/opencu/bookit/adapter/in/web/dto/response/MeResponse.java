package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.user.Role;
import com.opencu.bookit.domain.model.user.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Schema(
        description = """
    Describes important user's data
    Roles show which privileges a user has.
    Roles, possible values:
        ROLE_SYSTEM_USER - responsible for the internal logic of the program,
        such as making system bookings for events;
        ROLE_USER - an average user that uses the app;
        ROLE_ADMIN - controls the system and processes requests, but can't delete users or 
        appoint someone as an administrator;
        ROLE_SUPERADMIN - he has the same privileges as an admin, also he can appoint someone as an administrator
        and much more.
    """
)
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