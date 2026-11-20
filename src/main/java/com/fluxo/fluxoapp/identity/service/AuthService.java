package com.fluxo.fluxoapp.identity.service;

import com.fluxo.fluxoapp.identity.dto.AuthResponse;
import com.fluxo.fluxoapp.identity.dto.LoginRequest;
import com.fluxo.fluxoapp.identity.dto.RegisterRequest;
import com.fluxo.fluxoapp.shared.security.JwtService;
import com.fluxo.fluxoapp.shared.web.ApiException;
import com.fluxo.fluxoapp.users.domain.User;
import com.fluxo.fluxoapp.users.dto.UserResponse;
import com.fluxo.fluxoapp.users.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    static final String TERMS_VERSION = "2026-01";

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final String dummyHash;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.dummyHash = encoder.encode("senha-falsa-para-igualar-tempo");
    }

    // Sem @Transactional de propósito: o saveAndFlush abre a própria transação e a violação de
    // unicidade (corrida entre dois cadastros) chega aqui como exceção tratável.
    public AuthResponse register(RegisterRequest req) {
        String email = User.normalizeEmail(req.email());
        if (users.existsByEmail(email)) {
            throw emailTaken();
        }
        User user = new User(req.fullName(), email, encoder.encode(req.password()), TERMS_VERSION);
        try {
            user = users.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw emailTaken();
        }
        return toResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = users.findByEmail(User.normalizeEmail(req.email())).orElse(null);
        // Sempre faz uma comparação BCrypt, para não revelar por tempo se o e-mail existe.
        boolean ok = encoder.matches(req.password(), user != null ? user.getPasswordHash() : dummyHash);
        if (user == null || !ok || user.getStatus() != User.Status.ACTIVE) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "E-mail ou senha incorretos");
        }
        return toResponse(user);
    }

    private ApiException emailTaken() {
        return new ApiException(HttpStatus.CONFLICT, "EMAIL_ALREADY_IN_USE", "Este e-mail já está cadastrado");
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(jwt.generateAccessToken(user.getId()), "Bearer",
                jwt.accessTokenSeconds(), UserResponse.from(user));
    }
}
