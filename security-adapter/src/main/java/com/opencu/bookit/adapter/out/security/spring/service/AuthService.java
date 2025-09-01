package com.opencu.bookit.adapter.out.security.spring.service;

import com.opencu.bookit.adapter.out.security.spring.jwt.JwtUtils;
import com.opencu.bookit.adapter.out.security.spring.payload.request.TelegramUser;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AuthService implements LoadAuthorizationInfoPort{
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final JwtUtils jwtUtils;
    private final TelegramAuthService telegramAuthService;
    private final ZoneId zoneId;

    public AuthService(
            LoadUserPort loadUserPort,
            SaveUserPort saveUserPort,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils,
            TelegramAuthService telegramAuthService,
            @Value("${booking.zone-id}") ZoneId zoneId
            ) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
        this.jwtUtils = jwtUtils;
        this.telegramAuthService = telegramAuthService;
        this.zoneId = zoneId;
    }

    @Transactional
    public JwtResponse authorizeTelegramUser(String initDataRaw) {

        Map<String, String> preparedTelegramData = parseTelegramInitData(initDataRaw);

        telegramAuthService.validate(preparedTelegramData);
        
        TelegramUser telegramRequest = TelegramUser.fromMap(preparedTelegramData);
        UserModel user = findOrCreateUser(telegramRequest);
        Authentication authentication = createAuthentication(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return generateJwtForUser(user, authentication);
    }

    private Map<String, String> parseTelegramInitData(String initDataRaw) {
        Map<String, String> result = new HashMap<>();
        String[] pairs = initDataRaw.split("&");

        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                try {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    result.put(key, value);
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("Failed to decode Telegram init data", e);
                }
            }
        }
        return result;
    }

    private UserModel findOrCreateUser(TelegramUser telegramRequest) {
        return loadUserPort.findByTgId(telegramRequest.id())
                           .map(existingUser -> updateUserFromTelegramData(existingUser, telegramRequest))
                           .orElseGet(() -> createNewUserFromTelegramData(telegramRequest));
    }

    private UserModel createNewUserFromTelegramData(TelegramUser telegramRequest) {
        UserModel user = new UserModel();
        user.setTgId(telegramRequest.id());
        user.setRoles(Set.of(Role.ROLE_USER));
        user.setStatus(UserStatus.CREATED);
        user.setCreatedAt(LocalDateTime.now(zoneId));

        return updateUserFromTelegramData(user, telegramRequest);
    }

    private UserModel updateUserFromTelegramData(UserModel user, TelegramUser telegramRequest) {
        boolean updated = false;

        updated |= updateField(user.getUsername(), buildSafeUsername(telegramRequest), user::setUsername);
        updated |= updateField(user.getFirstName(), telegramRequest.firstName(), user::setFirstName);
        updated |= updateField(user.getLastName(), telegramRequest.lastName(), user::setLastName);
        updated |= updateField(user.getPhotoUrl(), telegramRequest.photoUrl(), user::setPhotoUrl);

        if (updated) {
            user.setUpdatedAt(LocalDateTime.now(zoneId));
            return saveUserPort.save(user);
        }
        return user;
    }

    private <T> boolean updateField(T currentValue, T newValue, java.util.function.Consumer<T> setter) {
        if (!java.util.Objects.equals(currentValue, newValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    private Authentication createAuthentication(UserModel user) {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private JwtResponse generateJwtForUser(UserModel user, Authentication authentication) {
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

    private String buildSafeUsername(TelegramUser request) {
        return (request.username() == null || request.username().isBlank())
                ? ";tg_id_" + request.id()
                : request.username() + ";tg_id_" + request.id();
    }

    @Transactional
    public MessageResponse completeUserProfile(UserProfileUpdateRequest profileRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserModel currentUser = loadUserPort.findByTgId(userDetails.getTgId())
                                            .orElseThrow(() -> new IllegalStateException("User not found"));

        if (currentUser.getStatus() == UserStatus.VERIFIED) {
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
}