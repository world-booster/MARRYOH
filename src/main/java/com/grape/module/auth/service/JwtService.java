package com.grape.module.auth.service;

import com.grape.security.jwt.JwtKeyProvider;
import com.grape.security.jwt.JwtProperties;
import com.grape.module.auth.exception.AuthErrorCode;
import com.grape.module.auth.exception.AuthException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtKeyProvider jwtKeyProvider;
    private final JwtProperties jwtProperties;

    public String generateAccessToken(String userKey) {
        if (userKey == null || userKey.isBlank()) {
            throw new AuthException(AuthErrorCode.TOKEN_MISSING);
        }

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject("access-token")
                .setIssuer(jwtProperties.getIssuer())
                .claim("userKey",userKey)
                .claim(jwtProperties.getRoleClaimName(), jwtProperties.getRoleUserClaimValue())
                .claim("type", jwtProperties.getAccessTokenType())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtProperties.getAccessTokenExpireMillis()))
                .signWith(jwtKeyProvider.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

}