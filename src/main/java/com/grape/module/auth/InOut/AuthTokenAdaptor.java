package com.grape.module.auth.InOut;

import com.grape.module.auth.service.AuthService;
import com.grape.module.user.InOut.AuthTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthTokenAdaptor implements AuthTokenPort {

    private final AuthService authService;

    @Override
    public String createRefreshToken(String userKey) {
        return authService.createRefreshToken(userKey);
    }
}
