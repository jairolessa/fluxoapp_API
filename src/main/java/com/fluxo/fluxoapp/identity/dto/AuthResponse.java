package com.fluxo.fluxoapp.identity.dto;

import com.fluxo.fluxoapp.users.dto.UserResponse;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds, UserResponse user) {
}
