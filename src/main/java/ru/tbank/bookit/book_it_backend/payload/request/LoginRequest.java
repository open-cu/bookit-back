package ru.tbank.bookit.book_it_backend.payload.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Класс для запроса входа в систему
 */
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // Геттеры и сеттеры

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
