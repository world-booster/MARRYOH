package com.grape.config.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtValidator {
    private final JwtParser jwtParser;

    public boolean isValidAccessToken(String token) {
        try{
            Claims claims = jwtParser.parse(token);

            return claims.getExpiration().after(new Date())
                    && "access".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }
}
