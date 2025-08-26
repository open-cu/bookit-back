package com.opencu.bookit.adapter.out.security.spring.service;

import com.opencu.bookit.adapter.out.security.spring.exception.TelegramValidationException;
import com.opencu.bookit.adapter.out.security.spring.payload.request.TelegramUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class TelegramAuthService {

    @Value("${tg-bot.token}")
    private String botToken;

    /**
     * Validates the authentication data received from Telegram.
     * Throws an exception if validation fails.
     *
     * @param authData The DTO containing all fields from the Telegram login widget.
     */
    public void validate(TelegramUserRequest authData) {
        final String dataCheckString = buildDataCheckString(authData);
        final String computedHash = computeHash(dataCheckString);

        if (!computedHash.equals(authData.hash())) {
            throw new TelegramValidationException("Hash validation failed. The data may be tampered with.");
        }
    }

    private String buildDataCheckString(TelegramUserRequest authData) {
        Map<String, String> dataMap = new TreeMap<>();
        dataMap.put("id", String.valueOf(authData.id()));
        dataMap.put("first_name", authData.firstName());
        dataMap.put("auth_date", String.valueOf(authData.authDate()));

        if (authData.lastName() != null) dataMap.put("last_name", authData.lastName());
        if (authData.username() != null) dataMap.put("username", authData.username());
        if (authData.photoUrl() != null) dataMap.put("photo_url", authData.photoUrl());

        return dataMap.entrySet().stream()
                      .map(entry -> entry.getKey() + "=" + entry.getValue())
                      .collect(Collectors.joining("\n"));
    }

    private String computeHash(String dataCheckString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] secretKey = digest.digest(botToken.getBytes(StandardCharsets.UTF_8));

            Mac hmacInstance = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            hmacInstance.init(secretKeySpec);

            byte[] computedHashBytes = hmacInstance.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(computedHashBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HMAC-SHA256 algorithm not available.", e);
        }
    }
}