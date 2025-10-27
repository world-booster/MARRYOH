package com.grape.module.auth.controller;

import com.grape.module.auth.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/token-verify")
    public ResponseEntity<String> tokenVerify(@RequestHeader("Authorization") String token){
        token = token.replace("Bearer", "");
        if(authService.verifyTempToken(token)){
            return ResponseEntity.ok("임시 인증 성공");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않거나 만료된 토큰입니다");
    }
}
