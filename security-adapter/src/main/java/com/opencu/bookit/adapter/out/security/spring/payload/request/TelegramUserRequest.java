package com.opencu.bookit.adapter.out.security.spring.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

/**
 * DTO for Telegram authentication data.
 *
 * @param id        Unique user identifier.
 * @param firstName User's first name.
 * @param lastName  User's last name (optional).
 * @param username  Telegram username (optional).
 * @param photoUrl  URL of the user's avatar (optional).
 * @param authDate  Authentication date in Unix time format (seconds).
 * @param hash      Hash string for data validation.
 */
public record TelegramUserRequest(
        @Positive(message = "ID must be a positive number")
        @JsonProperty("id") long id,

        @NotBlank(message = "First name cannot be blank")
        @JsonProperty("first_name") String firstName,

        @JsonProperty("last_name") String lastName,

        @JsonProperty("username") String username,

        @URL(message = "Photo URL must be a valid URL")
        @JsonProperty("photo_url") String photoUrl,

        @Positive(message = "Auth date must be a positive number")
        @JsonProperty("auth_date") long authDate,

        @NotBlank(message = "Hash cannot be blank")
        @JsonProperty("hash") String hash
) {
}