package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.PatchUserRequest;
import com.opencu.bookit.adapter.in.web.dto.request.UpdateProfileRequest;
import com.opencu.bookit.adapter.in.web.dto.response.MeResponse;
import com.opencu.bookit.adapter.in.web.exception.ProfileNotCompletedException;
import com.opencu.bookit.adapter.in.web.mapper.MeResponseMapper;
import com.opencu.bookit.application.port.out.qr.GenerateQrCodePort;
import com.opencu.bookit.application.port.out.user.LoadAuthorizationInfoPort;
import com.opencu.bookit.application.service.user.UserService;
import com.opencu.bookit.domain.model.user.UserModel;
import com.opencu.bookit.domain.model.user.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserControllerV1 {
    private final UserService userService;
    private final GenerateQrCodePort generateQrCodePort;
    private final LoadAuthorizationInfoPort loadAuthorizationInfoPort;
    private final MeResponseMapper meResponseMapper;

    public UserControllerV1(UserService userService, GenerateQrCodePort generateQrCodePort,
                            LoadAuthorizationInfoPort loadAuthorizationInfoPort, MeResponseMapper meResponseMapper) {
        this.userService = userService;
        this.generateQrCodePort = generateQrCodePort;
        this.loadAuthorizationInfoPort = loadAuthorizationInfoPort;
        this.meResponseMapper = meResponseMapper;
    }

    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    public ResponseEntity<MeResponse> getCurrentUser() {
        return ResponseEntity.ok(meResponseMapper.toResponse(loadAuthorizationInfoPort.getCurrentUser()));
    }

    @Operation(summary = "Update current user profile")
    @PutMapping("/me")
    public ResponseEntity<MeResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserModel updated = userService.updateProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone()
        );
        return ResponseEntity.ok(meResponseMapper.toResponse(updated));
    }

    @Operation(summary = "Get QR code for current user")
    @GetMapping("/me/qr")
    public ResponseEntity<byte[]> getCurrentUserQrCode() {
        UserModel currentUser = loadAuthorizationInfoPort.getCurrentUser();
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

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getSuperadmin())")
    @Operation(
            summary = "Get list of users with or without filters. FOR SUPERADMINS ONLY!"
    )
    @GetMapping
    public ResponseEntity<Page<MeResponse>> getUsers(
            @RequestParam(required = false) @Email String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Set<String> role,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "${pagination.default-page}") int page,
            @RequestParam(defaultValue = "${pagination.default-size}") int size
    ) {
        Sort.Direction direction = Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "firstName"));
        Page<MeResponse> adminsPage = userService
                .findWithFilters(email, phone, role, search, pageable)
                .map(meResponseMapper::toResponse);
        return ResponseEntity.ok(adminsPage);
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getSuperadmin())")
    @Operation(
            summary = "Get user's profile by its id. FOR SUPERADMINS ONLY!"
    )
    @GetMapping("/{userId}")
    public ResponseEntity<MeResponse> getById(
            @PathVariable UUID userId
    ) {
        Optional<UserModel> userOpt = userService.findById(userId);
        return userOpt.map(userModel -> ResponseEntity.ok(meResponseMapper.toResponse(
                userModel
        ))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getSuperadmin())")
    @Operation(summary = "Changing status by userId. FOR SUPERADMINS ONLY!")
    @PatchMapping("/{userId}")
    public ResponseEntity<MeResponse> patchStatus(
            @PathVariable UUID userId,
            @RequestBody PatchUserRequest patchUserRequest
    ) {
        UserModel patched = userService.patchUser(
                userId,
                patchUserRequest.firstName(),
                patchUserRequest.lastName(),
                patchUserRequest.email(),
                patchUserRequest.roles(),
                patchUserRequest.userStatus()
        );
        return ResponseEntity.ok(meResponseMapper.toResponse(patched));
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getSuperadmin())")
    @Operation(
            summary = "Delete user from database. FOR SUPERADMINS ONLY!"
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteById(
            @PathVariable UUID userId
    ) {
        userService.deleteById(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}