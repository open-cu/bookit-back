package com.opencu.bookit.adapter.in.web.controller;

import com.opencu.bookit.adapter.in.web.dto.request.UpdateProfileRequest;
import com.opencu.bookit.adapter.in.web.dto.response.MeResponse;
import com.opencu.bookit.adapter.in.web.mapper.MeResponseMapper;
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
    private final MeResponseMapper meResponseMapper;

    public UserController(UserService userService, LoadAuthorizationInfoPort loadAuthorizationInfoPort,
                          MeResponseMapper meResponseMapper) {
        this.userService = userService;
        this.loadAuthorizationInfoPort = loadAuthorizationInfoPort;
        this.meResponseMapper = meResponseMapper;
    }

    @Operation(summary = "Получить свой профиль")
    @GetMapping("/me")
    public ResponseEntity<MeResponse> getCurrentUser() {
        return ResponseEntity.ok(meResponseMapper.toResponse(loadAuthorizationInfoPort.getCurrentUser()));
    }

    @Operation(summary = "Получить пользователя по id (админ)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<MeResponse> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(meResponseMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Обновить свои данные")
    @PutMapping("/me")
    public ResponseEntity<MeResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        UserModel updated = userService.updateProfile(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone()
        );
        return ResponseEntity.ok(meResponseMapper.toResponse(updated));
    }
}