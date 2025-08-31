package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.out.security.spring.payload.request.UserProfileUpdateRequest;
import com.opencu.bookit.adapter.out.security.spring.payload.response.JwtResponse;
import com.opencu.bookit.adapter.out.security.spring.payload.response.MessageResponse;
import com.opencu.bookit.adapter.out.security.spring.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 {
    private final AuthService authService;

    public AuthControllerV1(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Telegram user basic authentication")
    @PostMapping("/telegram")
    public ResponseEntity<JwtResponse> authenticateTelegramUser(@RequestParam Map<String, String> telegramUserData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.authorizeTelegramUser(telegramUserData));
    }

    @Operation(summary = "Complete user profile with additional information")
    @PostMapping("/complete-profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> completeUserProfile(@Valid @RequestBody UserProfileUpdateRequest profileRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.completeUserProfile(profileRequest));
    }
}
