package ru.tbank.bookit.book_it_backend.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    @Size(max = 255)
    private String firstName;

    @Size(max = 255)
    private String lastName;

    @Email
    private String email;

    @Pattern(regexp = "^(\\+7\\d{10}|8\\d{10})$", message = "Invalid phone")
    private String phone;

    @JsonProperty("tg_id")
    private Long tgId;

    private String photoUrl;
}