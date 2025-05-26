package ru.tbank.bookit.book_it_backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.tbank.bookit.book_it_backend.model.UserStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Long tgId;
    private String photoUrl;
    private UserStatus status;
}