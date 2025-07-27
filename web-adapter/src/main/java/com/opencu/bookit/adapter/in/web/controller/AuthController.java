package com.opencu.bookit.adapter.in.web.controller;

import com.opencu.bookit.adapter.out.security.spring.payload.request.LoginRequest;
import com.opencu.bookit.adapter.out.security.spring.payload.request.SignupRequest;
import com.opencu.bookit.adapter.out.security.spring.payload.request.TelegramUserRequest;
import com.opencu.bookit.adapter.out.security.spring.payload.request.UserProfileUpdateRequest;
import com.opencu.bookit.adapter.out.security.spring.payload.response.JwtResponse;
import com.opencu.bookit.adapter.out.security.spring.payload.response.MessageResponse;
import com.opencu.bookit.adapter.out.security.spring.payload.response.UserProfileResponse;
import com.opencu.bookit.adapter.out.security.spring.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Telegram user basic authentication")
    @PostMapping("/tgUser")
    public ResponseEntity<JwtResponse> authenticateTelegramUser(@Valid @RequestBody TelegramUserRequest telegramUserRequest) {
        return ResponseEntity.ok(authService.authenticateTelegramUser(telegramUserRequest));
    }

    @Operation(summary = "Complete user profile with additional information")
    @PostMapping("/complete-profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> completeUserProfile(@Valid @RequestBody UserProfileUpdateRequest profileRequest) {
        return ResponseEntity.ok(authService.completeUserProfile(profileRequest));
    }

    @Operation(summary = "Get current user profile information")
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        return ResponseEntity.ok(authService.getCurrentUserProfile());
    }

    @Operation(summary = "User login")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(summary = "User registration")
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(authService.register(signupRequest));
    }
}