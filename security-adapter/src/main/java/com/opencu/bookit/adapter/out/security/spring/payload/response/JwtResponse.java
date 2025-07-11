package com.opencu.bookit.adapter.out.security.spring.payload.response;

import lombok.Data;

import java.util.UUID;

@Data
public class JwtResponse {
    private final String token;
    private final String type = "Bearer";
    private final UUID id;
    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;

    public JwtResponse(String accessToken, UUID id, String username, String email, String firstName, String lastName) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}