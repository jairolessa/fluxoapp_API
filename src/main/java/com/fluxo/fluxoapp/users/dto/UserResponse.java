package com.fluxo.fluxoapp.users.dto;

import com.fluxo.fluxoapp.users.domain.User;

import java.util.UUID;

public record UserResponse(UUID id, String fullName, String email, String preferredName, String timeZone) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getPreferredName(), u.getTimeZone());
    }
}
