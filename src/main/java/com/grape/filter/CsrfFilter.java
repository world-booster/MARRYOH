package com.grape.filter;

import com.grape.module.auth.adaptor.AuthPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CsrfFilter extends OncePerRequestFilter {

    private final AuthPort authPort;
    private static final Set<String> EXCLUDE_URLS = Set.of(
            "/api/user/signin",
            "/api/user/signout",
            "/api/user/signup",
            "/api/social/GOOGLE/login",
            "/api/social/KAKAO/login",
            "/api/social/NAVER/login",
            "/api/auth/refresh-token",
            "/api/auth/email-request",
            "/api/auth/email-verify"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if(EXCLUDE_URLS.contains(uri)){
            filterChain.doFilter(
                    request,
                    response
            );
            return;
        }

        String method = request.getMethod();

        if("GET".equalsIgnoreCase(method)
            || "HEAD".equalsIgnoreCase(method)
            || "OPTIONS".equalsIgnoreCase(method)
        ){
            filterChain.doFilter(
                    request,
                    response
            );
            return;
        }

        String headerToken = request.getHeader("X-CSRF-TOKEN");

        String cookieToken = getCookie(request, "CSRF-TOKEN");

        if(headerToken == null || cookieToken == null){
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF Token Mismatch");
            return;
        }

        if(!authPort.validateCsrfToken(headerToken)){
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF Token");
            return;
        }

        filterChain.doFilter(
                request,
                response
        );
    }

    private String getCookie(HttpServletRequest request, String name){
        if(request.getCookies() == null){
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
