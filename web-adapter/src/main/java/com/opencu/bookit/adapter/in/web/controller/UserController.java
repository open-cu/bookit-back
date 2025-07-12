package com.opencu.bookit.adapter.in.web.controller;

import com.opencu.bookit.adapter.in.web.dto.request.UpdateProfileRequest;
import com.opencu.bookit.application.port.out.user.LoadAuthorizationInfoPort;
import com.opencu.bookit.application.service.user.UserService;
import com.opencu.bookit.domain.model.user.UserModel;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final LoadAuthorizationInfoPort loadAuthorizationInfoPort;

    public UserController(UserService userService, LoadAuthorizationInfoPort loadAuthorizationInfoPort) {
        this.userService = userService;
        this.loadAuthorizationInfoPort = loadAuthorizationInfoPort;
    }

    @Operation(summary = "Получить свой профиль")
    @GetMapping("/me")
    public ResponseEntity<UserModel> getCurrentUser() {
        return ResponseEntity.ok(loadAuthorizationInfoPort.getCurrentUser());
    }

    @Operation(summary = "Получить пользователя по id (админ)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Обновить свои данные")
    @PutMapping("/me")
    public ResponseEntity<UserModel> updateProfile(@RequestBody UpdateProfileRequest request) {
        UserModel updated = userService.updateProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone()
        );
        return ResponseEntity.ok(updated);
    }
}