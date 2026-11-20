package com.fluxo.fluxoapp.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/** Id do usuário autenticado. Nunca aceite user_id vindo do corpo/URL: use sempre este. */
public final class CurrentUser {
    private CurrentUser() {}

    public static UUID id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UUID id)) {
            throw new IllegalStateException("Sem usuário autenticado");
        }
        return id;
    }
}
