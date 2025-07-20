package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.PatchUserRequest;
import com.opencu.bookit.adapter.in.web.dto.response.MeResponse;
import com.opencu.bookit.adapter.in.web.mapper.MeResponseMapper;
import com.opencu.bookit.application.service.user.UserService;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/admins")
public class AdminControllerV1 {
    private final UserService userService;
    private final MeResponseMapper meResponseMapper;

    public AdminControllerV1(UserService userService, MeResponseMapper meResponseMapper) {
        this.userService = userService;
        this.meResponseMapper = meResponseMapper;
    }

    @PreAuthorize("@securityService.hasRoleSuperAdminOrIsDev()")
    @GetMapping
    public ResponseEntity<Page<MeResponse>> getAdmins(
            @RequestParam(required = false) Set<String> role,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Sort.Direction direction = Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "firstName"));
        Page<MeResponse> adminsPage = userService
                .findWithFilters(role, search, pageable)
                .map(meResponseMapper::toResponse);
        return ResponseEntity.ok(adminsPage);
    }

    @PreAuthorize("@securityService.hasRoleSuperAdminOrIsDev()")
    @GetMapping("/{userId}")
    public ResponseEntity<MeResponse> getById(
            @PathVariable UUID userId
    ) {
        Optional<UserModel> userOpt = userService.findById(userId);
        return userOpt.map(userModel -> ResponseEntity.ok(meResponseMapper.toResponse(
                userModel
        ))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@securityService.hasRoleSuperAdminOrIsDev()")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteById(
            @PathVariable UUID userId
    ) {
        userService.deleteById(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

}
