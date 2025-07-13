package com.opencu.bookit.domain.model.user;

import com.opencu.bookit.domain.model.area.Review;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserModel)) return false;
        UserModel user = (UserModel) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
