package ru.tbank.bookit.book_it_backend.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.bookit.book_it_backend.DTO.UpdateProfileRequest;
import ru.tbank.bookit.book_it_backend.exception.ProfileNotCompletedException;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.model.UserStatus;
import ru.tbank.bookit.book_it_backend.service.HomeService;
import ru.tbank.bookit.book_it_backend.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserControllerV1 {
    private final UserService userService;
    private final HomeService homeService;

    public UserControllerV1(UserService userService, HomeService homeService) {
        this.userService = userService;
        this.homeService = homeService;
    }

    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @Operation(summary = "Update current user profile")
    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User updated = userService.updateProfile(
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
        User currentUser = userService.getCurrentUser();
        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            throw new ProfileNotCompletedException("User profile is not completed. Please complete your profile before accessing QR code.");
        }
        try {
            byte[] qrPng = homeService.generateUserQrCode(currentUser);
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