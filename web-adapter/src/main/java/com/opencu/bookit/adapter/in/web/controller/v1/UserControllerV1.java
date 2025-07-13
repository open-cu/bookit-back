package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.UpdateProfileRequest;
import com.opencu.bookit.adapter.in.web.exception.ProfileNotCompletedException;
import com.opencu.bookit.application.port.out.qr.GenerateQrCodePort;
import com.opencu.bookit.application.port.out.user.LoadAuthorizationInfoPort;
import com.opencu.bookit.application.service.user.UserService;
import com.opencu.bookit.domain.model.user.UserModel;
import com.opencu.bookit.domain.model.user.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/users")
public class UserControllerV1 {
    private final UserService userService;
    private final GenerateQrCodePort generateQrCodePort;
    private final LoadAuthorizationInfoPort loadAuthorizationInfoPort;

    public UserControllerV1(UserService userService, GenerateQrCodePort generateQrCodePort,
                            LoadAuthorizationInfoPort loadAuthorizationInfoPort) {
        this.userService = userService;
        this.generateQrCodePort = generateQrCodePort;
        this.loadAuthorizationInfoPort = loadAuthorizationInfoPort;
    }

    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    public ResponseEntity<UserModel> getCurrentUser() {
        return ResponseEntity.ok(loadAuthorizationInfoPort.getCurrentUser());
    }

    @Operation(summary = "Update current user profile")
    @PutMapping("/me")
    public ResponseEntity<UserModel> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserModel updated = userService.updateProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone()
        );
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get QR code for current user")
    @GetMapping("/me/qr")
    public ResponseEntity<byte[]> getCurrentUserQrCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserModel)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserModel currentUser = (UserModel) authentication.getPrincipal();
        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            throw new ProfileNotCompletedException("User profile is not completed. Please complete your profile before accessing QR code.");
        }

        try {
            byte[] qrPng = generateQrCodePort.generateUserQrCode(currentUser);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"user-" + currentUser.getId() + "-qr.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrPng);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code", e);
        }
    }
}