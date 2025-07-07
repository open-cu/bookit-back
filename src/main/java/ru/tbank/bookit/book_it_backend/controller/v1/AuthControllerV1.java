package ru.tbank.bookit.book_it_backend.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.payload.request.TelegramUserRequest;
import ru.tbank.bookit.book_it_backend.payload.request.UserProfileUpdateRequest;
import ru.tbank.bookit.book_it_backend.payload.response.JwtResponse;
import ru.tbank.bookit.book_it_backend.payload.response.MessageResponse;
import ru.tbank.bookit.book_it_backend.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 {
    private final AuthService authService;

    public AuthControllerV1(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Telegram user basic authentication")
    @PostMapping("/telegram")
    public ResponseEntity<JwtResponse> authenticateTelegramUser(@Valid @RequestBody TelegramUserRequest telegramUserRequest) {
        return ResponseEntity.ok(authService.authenticateTelegramUser(telegramUserRequest));
    }

    @Operation(summary = "Complete user profile with additional information")
    @PostMapping("/complete-profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> completeUserProfile(@Valid @RequestBody UserProfileUpdateRequest profileRequest) {
        return ResponseEntity.ok(authService.completeUserProfile(profileRequest));
    }
}
