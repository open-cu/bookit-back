package com.opencu.bookit.adapter.out.security.spring.service;

import com.opencu.bookit.adapter.out.security.spring.jwt.JwtUtils;
import com.opencu.bookit.adapter.out.security.spring.payload.request.LoginRequest;
import com.opencu.bookit.adapter.out.security.spring.payload.request.SignupRequest;
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
    private final JwtUtils jwtUtils;
    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    public AuthService(
            AuthenticationManager authenticationManager,
            LoadUserPort userRepository, SaveUserPort saveUserPort,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils
    ) {
        this.authenticationManager = authenticationManager;
        this.loadUserPort = userRepository;
        this.saveUserPort = saveUserPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public JwtResponse authenticateTelegramUser(TelegramUserRequest telegramUserRequest) {
        UserModel user = loadUserPort.findByTgId(telegramUserRequest.getId()).orElse(null);

        if (user == null) {
            user = new UserModel();
            user.setTgId(telegramUserRequest.getId());
            String safeUsername = telegramUserRequest.getUsername() + "tg_" + telegramUserRequest.getId();
            user.setUsername(safeUsername);
            user.setFirstName(telegramUserRequest.getFirstName());
            user.setLastName(telegramUserRequest.getLastName());
            user.setPhotoUrl(telegramUserRequest.getPhotoUrl());
            user.setStatus(UserStatus.CREATED);
            user.setRoles(Set.of(Role.ROLE_USER));
            user.setCreatedAt(LocalDateTime.now(zoneId));

            String randomPassword = generateRandomPassword();
            user.setPasswordHash(passwordEncoder.encode(randomPassword));

            user = saveUserPort.save(user);
        } else {
            String newUsername = telegramUserRequest.getUsername() != null
                    ? telegramUserRequest.getUsername()
                    : "tg_" + telegramUserRequest.getId();
            if (!newUsername.equals(user.getUsername())) {
                user.setUsername(newUsername);
            }
            if (telegramUserRequest.getFirstName() != null &&
                    !telegramUserRequest.getFirstName().equals(user.getFirstName())) {
                user.setFirstName(telegramUserRequest.getFirstName());
            }
            if (telegramUserRequest.getLastName() != null &&
                    !telegramUserRequest.getLastName().equals(user.getLastName())) {
                user.setLastName(telegramUserRequest.getLastName());
            }
            if (telegramUserRequest.getPhotoUrl() != null &&
                    !telegramUserRequest.getPhotoUrl().equals(user.getPhotoUrl())) {
                user.setPhotoUrl(telegramUserRequest.getPhotoUrl());
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

    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserModel user = loadUserPort.findByUsername(userDetails.getUsername())
                                .orElseThrow(() -> new IllegalStateException("User not found"));

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
    public MessageResponse register(SignupRequest signUpRequest) {
        if (loadUserPort.existsByUsername(signUpRequest.getUsername())) {
            return new MessageResponse("The user name is already in use");
        }
        if (signUpRequest.getEmail() != null && loadUserPort.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("The email address is already in use");
        }
        if (signUpRequest.getPhone() != null && loadUserPort.existsByPhone(signUpRequest.getPhone())) {
            return new MessageResponse("The telephone is already in use");
        }

        UserModel user = new UserModel();
        user.setUsername(signUpRequest.getUsername());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setPhone(signUpRequest.getPhone());
        user.setTgId(signUpRequest.getTgId());
        user.setPhotoUrl(signUpRequest.getPhotoUrl());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setStatus(UserStatus.CREATED);
        user.setCreatedAt(LocalDateTime.now(zoneId));

        saveUserPort.save(user);

        return new MessageResponse("The registration has been completed");
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}