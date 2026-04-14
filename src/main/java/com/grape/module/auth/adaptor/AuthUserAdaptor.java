package com.grape.module.auth.adaptor;

import com.grape.module.auth.service.TokenService;
import com.grape.module.user.adaptor.UserAuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserAdaptor implements UserAuthPort {

    private final TokenService tokenService;

    @Override
    public String createRefreshToken(String userKey) {
        return tokenService.createRefreshToken(userKey);
    }

    @Override
    public String jwtTokenGenerator(String userKey) { return tokenService.jwtTokenGenerator(userKey); }

    @Override
    public int deleteTokenByToken(String refreshToken) { return tokenService.deleteTokenByToken(refreshToken);}
}
