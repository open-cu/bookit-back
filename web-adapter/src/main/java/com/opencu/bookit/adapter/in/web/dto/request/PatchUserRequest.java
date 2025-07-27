package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.user.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        example = """
                {
                    "firstName": "Ivan",
                    "lastName": "Ivanov",
                    "email": "ivan_ivanov@example.com",
                    "roles": ["ROLE_ADMIN"],
                    "userStatus": "VERIFIED"
                }
                """
)
public record PatchUserRequest(
        @Schema(
                requiredMode = Schema.RequiredMode.AUTO,
                type = "string",
                example = "Ivan",
                accessMode =  Schema.AccessMode.READ_ONLY
        )
        String firstName,

        @Schema(
                requiredMode = Schema.RequiredMode.AUTO,
                type = "string",
                example = "Ivan",
                accessMode =  Schema.AccessMode.READ_ONLY
        )
        String lastName,

        @Schema(
                requiredMode = Schema.RequiredMode.AUTO,
                type = "string",
                format = "email",
                example = "ivan_ivanov@example.com",
                accessMode =  Schema.AccessMode.READ_ONLY
        )
        String email,

        @Schema(
                requiredMode = Schema.RequiredMode.AUTO,
                type = "array",
                example = "[ROLE_ADMIN]",
                accessMode =  Schema.AccessMode.READ_ONLY
        )
        List<String> roles,

        @Schema(
                requiredMode = Schema.RequiredMode.AUTO,
                type = "string",
                example = "VERIFIED",
                accessMode =  Schema.AccessMode.READ_ONLY
        )
        UserStatus userStatus
) {}
