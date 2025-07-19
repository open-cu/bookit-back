package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.response.MeResponse;
import com.opencu.bookit.adapter.in.web.mapper.MeResponseMapper;
import com.opencu.bookit.adapter.out.security.spring.payload.response.UserProfileResponse;
import com.opencu.bookit.application.service.user.UserService;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/admins")
public class AdminController {
    private final UserService userService;
    private final MeResponseMapper meResponseMapper;

    public AdminController(UserService userService, MeResponseMapper meResponseMapper) {
        this.userService = userService;
        this.meResponseMapper = meResponseMapper;
    }

    @GetMapping
    public ResponseEntity<Page<MeResponse>> getAdmins(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Sort.Direction direction = Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "firstName"));
        Page<MeResponse> adminsPage = userService
                .findWithFilters(search, pageable)
                .map(meResponseMapper::toResponse);
        return ResponseEntity.ok(adminsPage);
    }

}
