package com.fluxo.fluxoapp.users.service;

import com.fluxo.fluxoapp.shared.web.ApiException;
import com.fluxo.fluxoapp.users.domain.User;
import com.fluxo.fluxoapp.users.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    @Transactional(readOnly = true)
    public User get(UUID id) {
        return users.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND", "Sessão inválida"));
    }

    @Transactional
    public User changePreferredName(UUID id, String preferredName) {
        User user = get(id);
        user.changePreferredName(preferredName);
        return user;
    }
}
