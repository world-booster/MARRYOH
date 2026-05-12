package com.grape.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtParser {

    private final JwtKeyProvider jwtKeyProvider;

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtKeyProvider.getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
