package com.grape.module.social.controller;

import com.grape.module.social.model.*;
import com.grape.module.social.service.SocialLoginService;
import com.grape.module.social.service.SocialSignupService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/social")
public class SocialController {

    private final SocialLoginService socialLoginService;
    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @PostMapping("/{provider}/login")
    public ResponseEntity<SocialLoginResult> login(
            @PathVariable String provider,
            @RequestBody Map<String, String> request,
            HttpServletResponse response
            ) {
        SocialLoginCommand command = SocialLoginCommand.builder()
                .provider(SocialProvider.from(provider))
                .code(request.get("code"))
                .state(request.get("state"))
                .build();

        SocialLoginResult result = socialLoginService.login(command);

        Cookie cookie = new Cookie("REFRESH-TOKEN", result.getRefreshToken());
        cookie.setSecure(cookieSecure);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14);
        response.addCookie(cookie);

        Cookie accessCookie =
                new Cookie("ACCESS-TOKEN", result.getAccessToken());

        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(cookieSecure);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60);

        response.addCookie(accessCookie);

        return ResponseEntity.ok(result);
    }
}
