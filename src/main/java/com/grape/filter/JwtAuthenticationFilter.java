package com.grape.filter;

import com.grape.security.auth.CustomUserPrincipal;
import com.grape.security.jwt.JwtAuthenticationProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);

    private final JwtAuthenticationProvider authenticationProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        logger.info("JWT FILTER EXECUTED: {}", request.getRequestURI());

        String path = request.getServletPath();

        if(path.startsWith("/ws/")){
            filterChain.doFilter(request, response);
            return;
        }

        try{
            String token = getAccessToken(request);

            logger.info("TOKEN HASH= {}", token == null ? "NULL" : token.substring(0,20));

            if(token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.info("TRY AUTHENTICATE");
                Authentication authentication = authenticationProvider.authenticate(token, request);

                logger.info("AUTH RESULT = {}", authentication);

                if(authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    CustomUserPrincipal user = (CustomUserPrincipal) authentication.getPrincipal();


                    logger.info("AUTH USER = {} / {}", user.getUserKey(), user.getEmail());
                }
            }
        } catch (Exception e) {
            logger.error("JWT FILTER ERROR", e);

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){

        String path = request.getServletPath();

        return path.equals("/api/user/signin")
                || path.equals("/api/user/signup")
                || path.equals("/api/auth/refresh-token");
    }

    private String getAccessToken(HttpServletRequest request) {
        if(request.getCookies() == null) return null;

        for(var cookie : request.getCookies()) {
            if("ACCESS-TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
