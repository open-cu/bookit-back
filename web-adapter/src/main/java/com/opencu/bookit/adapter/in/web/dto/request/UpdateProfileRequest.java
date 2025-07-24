package com.opencu.bookit.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Schema(
        example = """
                    {
                        "firstName": "Ivan",
                        "lastName": "Ivanov",
                        "email": "ivan_ivanov@exaxmple.com",
                        "phone": "+79999999999"
                    }
                  """
)
public class UpdateProfileRequest {
    @NotBlank
    @Schema(
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            type = "string",
            example = "Ivan",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String firstName;

    @NotBlank
    @Schema(
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            type = "string",
            example = "Ivanov",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String lastName;

    @Email
    @Schema(
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            type = "string",
            format = "email",
            example = "ivan.ivanov@example.com",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String email;

    @Pattern(regexp = "^(\\+7\\d{10}|8\\d{10})$", message = "Invalid phone")
    @Schema(
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            type = "string",
            example = "+79999999999",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String phone;
}