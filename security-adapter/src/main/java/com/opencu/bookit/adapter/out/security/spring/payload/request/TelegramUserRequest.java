package com.opencu.bookit.adapter.out.security.spring.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Validator VALIDATOR;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            VALIDATOR = factory.getValidator();
        }
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static TelegramUserRequest fromMap(Map<String, String> telegramInitData) {
        String userJson = telegramInitData.get("user");
        if (userJson == null) {
            throw new IllegalArgumentException("User data not found in Telegram init data.");
        }

        TelegramUserRequest request;
        try {
            request = OBJECT_MAPPER.readValue(userJson, TelegramUserRequest.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse user JSON from Telegram init data", e);
        }

        Set<ConstraintViolation<TelegramUserRequest>> violations = VALIDATOR.validate(request);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Telegram data validation failed.", violations);
        }

        return request;
    }
}