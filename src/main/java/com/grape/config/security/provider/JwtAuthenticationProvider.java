package com.grape.config.security.provider;

import com.grape.config.security.jwt.JwtParser;
import com.grape.config.security.jwt.JwtValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    private final JwtParser jwtParser;
    private final JwtValidator jwtValidator;
    private final UserDetailsService userDetailsService;

    public Authentication authenticate(String token, HttpServletRequest request) {
        if(!jwtValidator.isValidAccessToken(token)) {
            return null;
        }

        String userKey = jwtParser.parse(token).getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(userKey);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        return authentication;
    }
}
