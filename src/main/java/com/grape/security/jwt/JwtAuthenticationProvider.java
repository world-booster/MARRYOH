package com.grape.security.jwt;

import com.grape.module.auth.exception.AuthErrorCode;
import com.grape.module.auth.exception.AuthException;
import com.grape.security.auth.CustomUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    private final JwtParser jwtParser;
    private final UserDetailsService userDetailsService;
    private final JwtProperties jwtProperties;

    public Authentication authenticate(String token, HttpServletRequest request) {
        try{
            Claims claims = jwtParser.parse(token);

            String type = claims.get("type", String.class);
            if(type == null || !jwtProperties.getAccessTokenType().equalsIgnoreCase(type)) {
                return null;
            }

            String userKey = claims.get("userKey", String.class);

            CustomUserPrincipal user = (CustomUserPrincipal) userDetailsService.loadUserByUsername(userKey);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            return authentication;
        }catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        }catch (JwtException e) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID_SIGNATURE);
        }
    }
}
