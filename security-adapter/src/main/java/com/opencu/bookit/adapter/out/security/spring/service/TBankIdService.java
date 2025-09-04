package com.opencu.bookit.adapter.out.security.spring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencu.bookit.adapter.out.security.spring.payload.response.TBankUserInfoResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@Getter
@Service
public class TBankIdService {

    @Value("${tbank-id.client-id}")
    private String clientId;

    @Value("${tbank-id.client-secret}")
    private String clientSecret;

    @Value("${tbank-id.redirect-uri}")
    private String redirectUri;

    @Value("${tbank-id.api-base-url}")
    private String apiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> generateLoginData() {
        String state = UUID.randomUUID().toString();
        String url = UriComponentsBuilder
                .fromUriString(apiBaseUrl + "/auth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .queryParam("response_type", "code")
                .queryParam("scope", "profile phone")
                .toUriString();
        return Map.of("url", url, "state", state);
    }

    public String getAccessToken(String code) {
        String url = UriComponentsBuilder.fromUriString(apiBaseUrl + "/auth/token").toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Object token = response.getBody().get("access_token");
            return token != null ? token.toString() : null;
        }
        throw new IllegalStateException("Failed to get access_token from TBank: " + response.getStatusCode());
    }

    public TBankUserInfoResponse getUserInfo(String accessToken) {
        String url = UriComponentsBuilder.fromUriString(apiBaseUrl + "/userinfo/userinfo").toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            return objectMapper.convertValue(body, TBankUserInfoResponse.class);
        }
        throw new IllegalStateException("Failed to get user info from TBank: " + response.getStatusCode());
    }
}