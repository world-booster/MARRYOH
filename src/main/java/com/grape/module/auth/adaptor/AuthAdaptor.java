package com.grape.module.auth.adaptor;

import com.grape.module.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthAdaptor implements AuthPort{

    private final AuthService authService;

    @Override
    public String createRefreshToken(String userKey) {
        return authService.createRefreshToken(userKey);
    }

    @Override
    public String createAccessToken(String userKey) { return authService.jwtTokenGenerator(userKey); }

    @Override
    public String jwtTokenGenerator(String userKey) {
        return authService.jwtTokenGenerator(userKey);
    }

    @Override
    public int deleteTokenByToken(String refreshToken) {
        return authService.deleteTokenByToken(refreshToken);
    }

    @Override
    public String createCsrfToken() { return authService.createCsrfToken(); }

    @Override
    public boolean validateCsrfToken(String csrfToken) { return authService.validateCsrfToken(csrfToken); }
}
