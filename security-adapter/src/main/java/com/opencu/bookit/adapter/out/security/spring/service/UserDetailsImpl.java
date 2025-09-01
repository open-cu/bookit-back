package com.opencu.bookit.adapter.out.security.spring.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencu.bookit.domain.model.user.UserModel;
import com.opencu.bookit.domain.model.user.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long tgId;
    private String username;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    private UserStatus status;

    public UserDetailsImpl(UUID id, Long tgId, String username, String email,
                           Collection<? extends GrantedAuthority> authorities, UserStatus status) {
        this.id = id;
        this.tgId = tgId;
        this.username = username;
        this.email = email;
        this.authorities = authorities;
        this.status = status;
    }

    @Override
    public String getPassword() {
        return null;
    }

    public static UserDetailsImpl build(UserModel user) {
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getTgId(),
                user.getUsername(),
                user.getEmail(),
                authorities,
                user.getStatus());
    }

    @Override
    public boolean isAccountNonExpired() {
        return status != UserStatus.DELETED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.BANNED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.VERIFIED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}