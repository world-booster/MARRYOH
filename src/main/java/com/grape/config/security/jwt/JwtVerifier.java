package com.grape.config.security.jwt;

import com.grape.module.auth.service.TokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtVerifier {

    private final TokenService tokenService;

    public Claims parse(String token) {
        return tokenService.parseJwtToken(token);
    }

    public boolean isValidAccessToken(String token) {
        return tokenService.verifyAccessToken(token);
    }

    public String getUserKey(String token){
        return parse(token).getSubject();
    }
}
