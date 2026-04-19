package com.grape.module.auth.adaptor;

import com.grape.module.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthAdaptor implements AuthPort{

    private final TokenService tokenService;

    @Override
    public String createRefreshToken(String userKey) {
        return tokenService.createRefreshToken(userKey);
    }

    @Override
    public String createAccessToken(String userKey) { return tokenService.jwtTokenGenerator(userKey); }

    @Override
    public String jwtTokenGenerator(String userKey) {
        return tokenService.jwtTokenGenerator(userKey);
    }

    @Override
    public int deleteTokenByToken(String refreshToken) {
        return tokenService.deleteTokenByToken(refreshToken);
    }
}
