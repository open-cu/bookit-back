package com.opencu.bookit.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    public enum RoleName {
        ROLE_USER,
        ROLE_ADMIN,
        ROLE_SUPERADMIN;

        public static RoleName fromString(String role) {
            if (role == null) {
                throw new IllegalArgumentException("Role name cannot be null");
            }
            String normalized = role.trim().toLowerCase();

            return switch (normalized) {
                case "user" -> ROLE_USER;
                case "admin" -> ROLE_ADMIN;
                case "superadmin" -> ROLE_SUPERADMIN;
                default -> throw new IllegalArgumentException("Unknown role name: " + role);
            };
        }
    }

    public static Set<RoleEntity> toRoleEntitySet(Set<String> roles) {
        Set<RoleEntity> result = new HashSet<>();
        for (var role: roles) {
            RoleEntity entity = new RoleEntity();
            entity.setName(RoleName.fromString(role));
            result.add(entity);
        }
        return result;
    }
}