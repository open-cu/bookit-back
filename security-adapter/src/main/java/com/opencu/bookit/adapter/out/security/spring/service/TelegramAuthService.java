package com.opencu.bookit.adapter.out.security.spring.service;

import com.opencu.bookit.adapter.out.security.spring.exception.TelegramValidationException;
import com.opencu.bookit.adapter.out.security.spring.payload.request.TelegramUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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
     * @param telegramUserData The map of query parameters from Telegram.
     */
    public void validate(@RequestParam Map<String, String> telegramUserData) {
        String hash = telegramUserData.get("hash");

        if (hash == null || hash.isEmpty()) {
            throw new TelegramValidationException("Missing hash parameter in Telegram data.");
        }

        final String dataCheckString = buildDataCheckString(telegramUserData);
        final String computedHash = computeHash(dataCheckString);

        if (!computedHash.equals(hash)) {
            throw new TelegramValidationException("Telegram hash validation failed.");
        }
    }

    private String buildDataCheckString(@RequestParam Map<String, String> telegramUserData) {
        Map<String, String> dataMap = new TreeMap<>();

        telegramUserData.forEach((key, value) -> {
            if (!key.equals("hash")) {
                dataMap.put(key, value);
            }
        });

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