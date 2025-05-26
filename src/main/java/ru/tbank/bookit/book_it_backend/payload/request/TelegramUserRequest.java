package ru.tbank.bookit.book_it_backend.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TelegramUserRequest {
    @NotNull
    private Long id; // Telegram ID

    private String first_name;

    private String last_name;

    private String username;

    private String language_code;

    private Boolean is_premium;

    private String photo_url;
}