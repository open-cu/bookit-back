package com.opencu.bookit.adapter.out.security.spring.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

import java.util.Map;
import java.util.Set;

/**
 * DTO for Telegram authentication data.
 *
 * @param id        Unique user identifier.
 * @param firstName User's first name.
 * @param lastName  User's last name (optional).
 * @param username  Telegram username (optional).
 * @param photoUrl  URL of the user's avatar (optional).
 */
public record TelegramUserRequest(
        @Positive(message = "ID must be a positive number")
        @JsonProperty("id") long id,

        @NotBlank(message = "First name cannot be blank")
        @JsonProperty("first_name") String firstName,

        @JsonProperty("last_name") String lastName,

        @JsonProperty("username") String username,

        @URL(message = "Photo URL must be a valid URL")
        @JsonProperty("photo_url") String photoUrl
) {
    public static TelegramUserRequest fromMap(Map<String, String> telegramUserData) {
        long id = Long.parseLong(telegramUserData.get("id"));
        String firstName = telegramUserData.get("first_name");
        String lastName = telegramUserData.get("last_name");
        String username = telegramUserData.get("username");
        String photoUrl = telegramUserData.get("photo_url");

        TelegramUserRequest request = new TelegramUserRequest(id, firstName, lastName, username, photoUrl);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TelegramUserRequest>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Telegram data validation failed.", violations);
        }

        return request;
    }
}