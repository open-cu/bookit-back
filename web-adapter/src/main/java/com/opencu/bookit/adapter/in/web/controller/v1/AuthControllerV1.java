package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.out.security.spring.payload.request.UserProfileUpdateRequest;
import com.opencu.bookit.adapter.out.security.spring.payload.response.JwtResponse;
import com.opencu.bookit.adapter.out.security.spring.payload.response.MessageResponse;
import com.opencu.bookit.adapter.out.security.spring.payload.response.TinkoffUserInfoResponse;
import com.opencu.bookit.adapter.out.security.spring.service.AuthService;
import com.opencu.bookit.adapter.out.security.spring.service.TinkoffIdService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 {
    private final AuthService authService;
    private final TinkoffIdService tinkoffIdService;

    public AuthControllerV1(AuthService authService, TinkoffIdService tinkoffIdService) {
        this.authService = authService;
        this.tinkoffIdService = tinkoffIdService;
    }

    @Operation(summary = "Telegram user basic authentication")
    @PostMapping("/telegram")
    public ResponseEntity<JwtResponse> authenticateTelegramUser(@RequestHeader(name = "Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("tma ")) {
            throw new IllegalArgumentException("Invalid Authorization header format");
        }
        // Extracting the data after the "tma" prefix
        String telegramInitData = authorizationHeader.substring(4);
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.authorizeTelegramUser(telegramInitData));
    }

    @Operation(summary = "Complete user profile with additional information")
    @PostMapping("/complete-profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> completeUserProfile(@Valid @RequestBody UserProfileUpdateRequest profileRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.completeUserProfile(profileRequest));
    }

    @Operation(summary = "Exchange Tinkoff authorization code for access token")
    @PostMapping("/tinkoff-token")
    public ResponseEntity<String> getTinkoffAccessToken(@RequestParam String code) {
        // TODO: получить access_token по code через TinkoffIdService
        return ResponseEntity.ok(tinkoffIdService.getAccessToken(code));
    }

    @Operation(summary = "Get Tinkoff user info by access token")
    @PostMapping("/userinfo")
    public ResponseEntity<TinkoffUserInfoResponse> getTinkoffUserInfo(@RequestParam String accessToken) {
        // TODO: получить userinfo по access_token через TinkoffIdService
        return ResponseEntity.ok(tinkoffIdService.getUserInfo(accessToken));
    }
}