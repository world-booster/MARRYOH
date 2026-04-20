package com.grape.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
@RequiredArgsConstructor
public class JwtParser {

    private final JwtKeyProvider jwtKeyProvider;

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtKeyProvider.getPublicKey())
                .build()
                .parseClaimsJwt(token)
                .getBody();
    }
}
