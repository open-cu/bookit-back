package com.opencu.bookit.domain.model.user;

import com.opencu.bookit.domain.model.area.Review;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private Long tgId;
    private String firstName;
    private String lastName;
    private String photoUrl;
    private String email;
    private String passwordHash;
    private String phone;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
    private Set<Role> roles = new HashSet<>();
    private List<Review> reviews = new ArrayList<>();
    public String getPassword() {
        return passwordHash;
    }
    public String getUsername() {
        return username;
    }

    public boolean isAccountNonExpired() {
        return status != UserStatus.DELETED;
    }
    public boolean isAccountNonLocked() {
        return status != UserStatus.BANNED;
    }
    public boolean isCredentialsNonExpired() {
        return true;
    }
    public boolean isEnabled() {
        return status == UserStatus.VERIFIED;
    }
}
