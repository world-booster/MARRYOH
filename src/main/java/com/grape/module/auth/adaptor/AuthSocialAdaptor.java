package com.grape.module.auth.adaptor;

import com.grape.module.auth.service.TokenService;
import com.grape.module.social.adaptor.SocialAuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthSocialAdaptor implements SocialAuthPort {

    private final TokenService tokenService;

    @Override
    public String createAccessToken(String userKey) {
        return tokenService.jwtTokenGenerator(userKey);
    }

    @Override
    public String createRefreshToken(String userKey) {
        return tokenService.createRefreshToken(userKey);
    }
}
