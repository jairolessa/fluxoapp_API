package com.fluxo.fluxoapp.shared.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private static final String SECRET = "segredo-somente-para-testes-com-pelo-menos-32-bytes-0123456789";

    @Test
    void roundTripReturnsSameUserId() {
        JwtService jwt = new JwtService(new JwtProperties(SECRET, 15));
        UUID id = UUID.randomUUID();
        assertEquals(id, jwt.parseUserId(jwt.generateAccessToken(id)));
    }

    @Test
    void rejectsTokenSignedWithAnotherSecret() {
        JwtService a = new JwtService(new JwtProperties(SECRET, 15));
        JwtService b = new JwtService(new JwtProperties("outro-segredo-diferente-com-pelo-menos-32-bytes-987654", 15));
        String token = b.generateAccessToken(UUID.randomUUID());
        assertThrows(JwtException.class, () -> a.parseUserId(token));
    }

    @Test
    void rejectsGarbage() {
        JwtService jwt = new JwtService(new JwtProperties(SECRET, 15));
        assertThrows(JwtException.class, () -> jwt.parseUserId("isso.nao.e-jwt"));
    }

    @Test
    void rejectsShortSecret() {
        assertThrows(IllegalStateException.class, () -> new JwtService(new JwtProperties("curto", 15)));
    }
}
