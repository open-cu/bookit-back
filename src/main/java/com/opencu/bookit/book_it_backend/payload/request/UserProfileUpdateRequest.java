package ru.tbank.bookit.book_it_backend.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^(\\+7\\d{10}|8\\d{10})$", message = "Incorrect phone number format")
    private String phone;

    @Email
    private String email;
}