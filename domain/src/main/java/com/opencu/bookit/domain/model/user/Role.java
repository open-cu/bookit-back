package com.opencu.bookit.domain.model.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private Long id;
    private RoleName name;

    public enum RoleName {
        ROLE_USER,
        ROLE_ADMIN
    }
}