package com.opencu.bookit.adapter.out.security.spring.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TelegramUserRequest {
    @NotNull
    private Long id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String username;

    @JsonProperty("language_code")
    private String languageCode;

    @JsonProperty("is_premium")
    private Boolean isPremium;

    @JsonProperty("photo_url")
    private String photoUrl;
}