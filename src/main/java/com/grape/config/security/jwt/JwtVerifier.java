package com.grape.config.security.jwt;

import com.grape.module.auth.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtVerifier {

    private final AuthService authService;

    public Claims parse(String token) {
        return authService.parseJwtToken(token);
    }

    public boolean isValidAccessToken(String token) {
        return authService.verifyAccessToken(token);
    }

    public String getUserKey(String token){
        return parse(token).getSubject();
    }
}
