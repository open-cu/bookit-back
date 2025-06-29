package ru.tbank.bookit.book_it_backend.payload.response;

import ru.tbank.bookit.book_it_backend.model.UserStatus;
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