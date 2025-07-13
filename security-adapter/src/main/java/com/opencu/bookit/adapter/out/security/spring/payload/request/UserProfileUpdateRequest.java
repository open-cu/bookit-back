package com.opencu.bookit.adapter.out.security.spring.payload.request;

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

    @NotBlank
    @Email
    private String email;
}