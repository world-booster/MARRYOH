package com.grape.module.auth.controller;

import com.grape.module.auth.service.IAuthService;
import com.grape.module.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    private final IAuthService authService;

    @PostMapping("/update-token")
    public ResponseEntity<?> updateToken(@CookieValue("REFRESH-TOKEN") String refreshToken) {
        try{
            Claims claims = authService.parseJwtToken(refreshToken);

            if(claims.getExpiration().before(new Date())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 만료");
            }

            String userKey = claims.getSubject();

            String storedHash = authService.selectRefreshToken(userKey);
            if(storedHash == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 없음");
            }

//            if(!authService.verifyRefreshToken(refreshToken, userKey)){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 불일치");
//            }

            int newRefreshTokenResult = authService.createRefreshToken(userKey);

            if(newRefreshTokenResult == 0){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 생성 실패");
            }

            String newAccessToken = authService.jwtTokenGenerator(userKey);
            String newRefreshToken = authService.selectRefreshToken(userKey);

            ResponseCookie cookie = ResponseCookie.from("REFRESH-TOKEN", newRefreshToken)
                    .httpOnly(true)
                    .secure(false) //HTTPS (개발단계 false)
                    .path("/")
                    .maxAge(7*24*60*60) //7일
                    .sameSite("Lax") // 개발단계 : Lax , 실무 : strict
                    .build();

            //보안을 위해 토큰 삭제 및 새로 생성하는 함수 추가하기

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("accessToken", newAccessToken, "message", "ok"));
        } catch (JwtException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 검증 실패: "+e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> reCreateAccessToken(@CookieValue("REFRESH-TOKEN") String refreshToken){

        try{
            if(refreshToken == null || refreshToken.isBlank()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰이 존재 하지 않습니다.");
            }

            String userKey = authService.selectUserKeyByToken(refreshToken);

            if(userKey == null || userKey.isEmpty()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh 토큰입니다.");
            }

            authService.deleteToken(userKey);
            authService.createRefreshToken(userKey);

            String newRefreshToken = authService.selectRefreshToken(userKey);
            String newAccessToken = authService.jwtTokenGenerator(userKey);

            if(newAccessToken == null || newAccessToken.isEmpty()){
                throw new RuntimeException("accessToken 발급 실패");
            }

            ResponseCookie cookie = ResponseCookie.from("REFRESH-TOKEN", newRefreshToken)
                    .httpOnly(true)
                    .secure(false) //HTTPS (개발단계 false)
                    .path("/")
                    .maxAge(7*24*60*60) //7일
                    .sameSite("Lax") // 개발단계 : Lax , 실무 : strict
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("accessToken", newAccessToken, "message", "ok"));

        } catch (JwtException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 검증 실패: "+e.getMessage());
        }
    }
}
