package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.out.security.spring.payload.request.UserProfileUpdateRequest;
import com.opencu.bookit.adapter.out.security.spring.payload.response.JwtResponse;
import com.opencu.bookit.adapter.out.security.spring.payload.response.MessageResponse;
import com.opencu.bookit.adapter.out.security.spring.payload.response.TBankUserInfoResponse;
import com.opencu.bookit.adapter.out.security.spring.service.AuthService;
import com.opencu.bookit.adapter.out.security.spring.service.TBankIdService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 {
    private final AuthService authService;
    private final TBankIdService tBankIdService;

    public AuthControllerV1(AuthService authService, TBankIdService tBankIdService) {
        this.authService = authService;
        this.tBankIdService = tBankIdService;
    }

    @Operation(summary = "Telegram user basic authentication")
    @PostMapping("/telegram")
    public ResponseEntity<JwtResponse> authenticateTelegramUser(@RequestHeader(name = "Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("tma ")) {
            throw new IllegalArgumentException("Invalid Authorization header format");
        }
        String telegramInitData = authorizationHeader.substring(4);
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.authorizeTelegramUser(telegramInitData));
    }

    @Operation(summary = "Complete user profile with additional information")
    @PostMapping("/complete-profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> completeUserProfile(@Valid @RequestBody UserProfileUpdateRequest profileRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.completeUserProfile(profileRequest));
    }

    @Operation(summary = "Get TBank ID login URL and save state in session")
    @GetMapping("/tbank-login")
    public ResponseEntity<Map<String, String>> getTBankLoginUrl(HttpSession session) {
        Map<String, String> loginData = tBankIdService.generateLoginData();
        session.setAttribute("TBANK_OAUTH_STATE", loginData.get("state"));
        return ResponseEntity.ok(loginData);
    }

    @Operation(summary = "Exchange TBank authorization code for access token, validating state")
    @PostMapping("/tbank-token")
    public ResponseEntity<String> getTBankAccessToken(
            @RequestParam String code,
            @RequestParam String state,
            HttpSession session
    ) {
        String savedState = (String) session.getAttribute("TBANK_OAUTH_STATE");
        if (savedState == null || !savedState.equals(state)) {
            throw new IllegalArgumentException("Invalid state parameter");
        }
        session.removeAttribute("TBANK_OAUTH_STATE");
        String token = tBankIdService.getAccessToken(code);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Get TBank user info by access token")
    @PostMapping("/userinfo")
    public ResponseEntity<TBankUserInfoResponse> getTBankUserInfo(@RequestParam String accessToken) {
        return ResponseEntity.ok(tBankIdService.getUserInfo(accessToken));
    }
}