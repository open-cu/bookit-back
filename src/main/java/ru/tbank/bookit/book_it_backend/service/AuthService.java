package ru.tbank.bookit.book_it_backend.service;

import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.model.UserStatus;
import ru.tbank.bookit.book_it_backend.payload.request.*;
import ru.tbank.bookit.book_it_backend.payload.response.JwtResponse;
import ru.tbank.bookit.book_it_backend.payload.response.MessageResponse;
import ru.tbank.bookit.book_it_backend.payload.response.UserProfileResponse;
import ru.tbank.bookit.book_it_backend.repository.UserRepository;
import ru.tbank.bookit.book_it_backend.security.jwt.JwtUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public JwtResponse authenticateTelegramUser(TelegramUserRequest telegramUserRequest) {
        User user = userRepository.findByTgId(telegramUserRequest.getId())
                .orElse(null);

        if (user == null) {
            user = new User();
            user.setTgId(telegramUserRequest.getId());
            user.setUsername(telegramUserRequest.getUsername() != null ?
                    telegramUserRequest.getUsername() : "tg_" + telegramUserRequest.getId());
            user.setFirstName(telegramUserRequest.getFirst_name());
            user.setLastName(telegramUserRequest.getLast_name());
            user.setPhotoUrl(telegramUserRequest.getPhoto_url());
            user.setStatus(UserStatus.CREATED);
            user.setCreatedAt(LocalDateTime.now());

            String randomPassword = generateRandomPassword();
            user.setPasswordHash(passwordEncoder.encode(randomPassword));

            userRepository.save(user);
        } else {
            if (telegramUserRequest.getUsername() != null) {
                user.setUsername(telegramUserRequest.getUsername());
            }
            if (telegramUserRequest.getFirst_name() != null) {
                user.setFirstName(telegramUserRequest.getFirst_name());
            }
            if (telegramUserRequest.getLast_name() != null) {
                user.setLastName(telegramUserRequest.getLast_name());
            }
            if (telegramUserRequest.getPhoto_url() != null) {
                user.setPhotoUrl(telegramUserRequest.getPhoto_url());
            }

            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
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
        User currentUser = getCurrentUser();
        if (currentUser.getStatus() != UserStatus.CREATED) {
            return new MessageResponse("User profile is already verified!");
        }
        if (profileRequest.getFirstName() == null || profileRequest.getLastName() == null ||
                profileRequest.getEmail() == null || profileRequest.getPhone() == null) {
            throw new IllegalArgumentException("All fields are required to complete the profile!");
        }

        currentUser.setFirstName(profileRequest.getFirstName());
        currentUser.setLastName(profileRequest.getLastName());
        currentUser.setPhone(profileRequest.getPhone());
        currentUser.setEmail(profileRequest.getEmail());

        currentUser.setStatus(UserStatus.VERIFIED);
        currentUser.setUpdatedAt(LocalDateTime.now());

        userRepository.save(currentUser);

        return new MessageResponse("Профиль пользователя успешно обновлен!");
    }

    public UserProfileResponse getCurrentUserProfile() {
        User user = getCurrentUser();

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

    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
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
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new MessageResponse("Ошибка: Имя пользователя уже занято!");
        }
        if (signUpRequest.getEmail() != null && userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("Ошибка: Email уже используется!");
        }
        if (signUpRequest.getPhone() != null && userRepository.existsByPhone(signUpRequest.getPhone())) {
            return new MessageResponse("Ошибка: Телефон уже используется!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setPhone(signUpRequest.getPhone());
        user.setTgId(signUpRequest.getTg_id());
        user.setPhotoUrl(signUpRequest.getPhotoUrl());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setStatus(UserStatus.CREATED);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return new MessageResponse("Пользователь успешно зарегистрирован!");
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }
}