package com.opencu.bookit.adapter.out.security.spring.service;

import com.opencu.bookit.adapter.out.security.spring.jwt.JwtUtils;
import com.opencu.bookit.adapter.out.security.spring.payload.request.TelegramUserRequest;
import com.opencu.bookit.adapter.out.security.spring.payload.request.UserProfileUpdateRequest;
import com.opencu.bookit.adapter.out.security.spring.payload.response.JwtResponse;
import com.opencu.bookit.adapter.out.security.spring.payload.response.MessageResponse;
import com.opencu.bookit.adapter.out.security.spring.payload.response.UserProfileResponse;
import com.opencu.bookit.application.port.out.user.LoadAuthorizationInfoPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.port.out.user.SaveUserPort;
import com.opencu.bookit.domain.model.user.Role;
import com.opencu.bookit.domain.model.user.UserModel;
import com.opencu.bookit.domain.model.user.UserStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService implements LoadAuthorizationInfoPort {
    private final AuthenticationManager authenticationManager;
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final PasswordEncoder passwordEncoder;
    private final TelegramAuthService telegramAuthService;
    private final JwtUtils jwtUtils;
    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    public AuthService(
            AuthenticationManager authenticationManager,
            LoadUserPort userRepository, SaveUserPort saveUserPort,
            PasswordEncoder passwordEncoder, TelegramAuthService telegramAuthService,
            JwtUtils jwtUtils
                      ) {
        this.authenticationManager = authenticationManager;
        this.loadUserPort = userRepository;
        this.saveUserPort = saveUserPort;
        this.passwordEncoder = passwordEncoder;
        this.telegramAuthService = telegramAuthService;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public JwtResponse authenticateTelegramUser(TelegramUserRequest telegramUserRequest) {
        telegramAuthService.validate(telegramUserRequest);

        UserModel user = loadUserPort.findByTgId(telegramUserRequest.id()).orElse(null);

        String safeUsername = (telegramUserRequest.username() == null || telegramUserRequest.username().isBlank())
                ? ";tg_id_" + telegramUserRequest.id()
                : telegramUserRequest.username() + ";tg_id_" + telegramUserRequest.id();

        if (user == null) {
            user = new UserModel();
            user.setTgId(telegramUserRequest.id());
            user.setUsername(safeUsername);
            user.setFirstName(telegramUserRequest.firstName());
            user.setLastName(telegramUserRequest.lastName());
            user.setPhotoUrl(telegramUserRequest.photoUrl());
            user.setStatus(UserStatus.CREATED);
            user.setRoles(Set.of(Role.ROLE_USER));
            user.setCreatedAt(LocalDateTime.now(zoneId));

            String randomPassword = generateRandomPassword();
            user.setPasswordHash(passwordEncoder.encode(randomPassword));

            user = saveUserPort.save(user);
        } else {
            if (!safeUsername.equals(user.getUsername())) {
                user.setUsername(safeUsername);
            }
            if (telegramUserRequest.firstName() != null &&
                    !telegramUserRequest.firstName().equals(user.getFirstName())) {
                user.setFirstName(telegramUserRequest.firstName());
            }
            if (telegramUserRequest.lastName() != null &&
                    !telegramUserRequest.lastName().equals(user.getLastName())) {
                user.setLastName(telegramUserRequest.lastName());
            }
            if (telegramUserRequest.photoUrl() != null &&
                    !telegramUserRequest.photoUrl().equals(user.getPhotoUrl())) {
                user.setPhotoUrl(telegramUserRequest.photoUrl());
            }
            user.setUpdatedAt(LocalDateTime.now(zoneId));
            saveUserPort.save(user);
        }

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        return new JwtResponse(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    @Transactional
    public MessageResponse completeUserProfile(UserProfileUpdateRequest profileRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserModel currentUser = loadUserPort.findByTgId(userDetails.getTgId())
                                            .orElseThrow(() -> new IllegalStateException("User not found"));

        if (currentUser.getStatus() != UserStatus.CREATED) {
            return new MessageResponse("The profile is verified");
        }
        if (profileRequest.getFirstName() == null || profileRequest.getLastName() == null ||
                profileRequest.getEmail() == null || profileRequest.getPhone() == null) {
            throw new IllegalArgumentException("All fields must be filled in");
        }

        currentUser.setFirstName(profileRequest.getFirstName());
        currentUser.setLastName(profileRequest.getLastName());
        currentUser.setPhone(profileRequest.getPhone());
        currentUser.setEmail(profileRequest.getEmail());

        currentUser.setStatus(UserStatus.VERIFIED);
        currentUser.setUpdatedAt(LocalDateTime.now(zoneId));

        saveUserPort.save(currentUser);

        return new MessageResponse("The profile has been updated");
    }

    public UserProfileResponse getCurrentUserProfile() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserModel user = loadUserPort.findByTgId(userDetails.getTgId())
                                     .orElseThrow(() -> new IllegalStateException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getTgId(),
                user.getPhotoUrl(),
                user.getStatus()
        );
    }

    public UserModel getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            Long tgId = userDetails.getTgId();
            return loadUserPort.findByTgId(tgId)
                               .orElseThrow(() -> new RuntimeException("User not found (tgId): " + tgId));
        }
        throw new RuntimeException("Unknown principal: " + principal);
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}