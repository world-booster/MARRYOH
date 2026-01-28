package com.grape.module.auth.controller;

import com.grape.module.auth.service.IAuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

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

    @PostMapping("/update-token")
    public ResponseEntity<?> updateAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        try{
            Claims claims = authService.parseJwtToken(refreshToken);
            if(!claims.getExpiration().after(new Date())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 만료");
            }

            String userKey = claims.getSubject();
            if(authService.checkUserToken(userKey) != 1){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 없음");
            }

            String storedToken = authService.selectRefreshToken(userKey);
            if(!storedToken.equals(refreshToken)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 불일치");
            }

            String newAccessToken = authService.jwtTokenGenerator(userKey);

            //보안을 위해 토큰 삭제 및 새로 생성하는 함수 추가하기

            return ResponseEntity.ok(Map.of(
                    "acccessToken", newAccessToken,
                    "refreshToken", storedToken
            ));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 갱신 실패: "+e.getMessage());
        }
    }
}
