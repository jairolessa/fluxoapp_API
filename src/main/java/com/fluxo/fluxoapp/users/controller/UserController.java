package com.fluxo.fluxoapp.users.controller;

import com.fluxo.fluxoapp.shared.security.CurrentUser;
import com.fluxo.fluxoapp.users.dto.PreferredNameRequest;
import com.fluxo.fluxoapp.users.dto.UserResponse;
import com.fluxo.fluxoapp.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public UserResponse me() {
        return UserResponse.from(service.get(CurrentUser.id()));
    }

    @PatchMapping("/preferred-name")
    public UserResponse changePreferredName(@Valid @RequestBody PreferredNameRequest req) {
        return UserResponse.from(service.changePreferredName(CurrentUser.id(), req.preferredName()));
    }
}
