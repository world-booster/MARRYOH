package com.grape.module.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grape.module.auth.service.AuthService;
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

    private final AuthService authService;
    private final ObjectMapper objectMapper; //임시 추후 DTO 형식으로 설계

    @PostMapping("/update-token")
    public ResponseEntity<?> updateToken(@CookieValue(value = "REFRESH-TOKEN", required = false) String refreshToken) {

        if(refreshToken == null || refreshToken.isBlank()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("RefreshToken is not existed.");
        }

        try{

            int check = authService.checkToken(refreshToken);
            if(check == 0){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("RefreshToken is not Valid.");
            }

            String userKey = authService.selectUserKeyByToken(refreshToken);

            String newAccessToken = authService.jwtTokenGenerator(userKey);
            String newRefreshToken = authService.createRefreshToken(userKey);

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

    @PostMapping("/email-request")
    public ResponseEntity<Map<String,String>> sendEmailCode(@RequestBody String body) {
        Map<String,String> response = new java.util.HashMap<>();
        try {
            String email = objectMapper.readTree(body).path("email").asText(null);
            if(email == null || email.isBlank()){
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "email 값이 필요합니다."));
            }

            authService.sendEmailVerification(email);
            response.put("message", "인증 코드가 전송되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("이메일 전송 실패: {}", e.getMessage());
            response.put("error", "이메일 전송 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/email-verify")
    public ResponseEntity<Map<String,String>> verifyEmailCode(@RequestParam String email, @RequestParam String code) {
        Map<String,String> response = new java.util.HashMap<>();
        if (authService.verifyEmailCode(email, code)) {
            response.put("message", "이메일 인증 완료");
            return ResponseEntity.ok(response);
        }
        response.put("error", "인증 실패 또는 만료된 코드입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> reCreateAccessToken(@CookieValue(value = "REFRESH-TOKEN", required = false) String refreshToken){

        if(refreshToken == null || refreshToken.isBlank()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰이 존재 하지 않습니다.");
        }

        try{
            int check = authService.checkToken(refreshToken);
            if(check == 0){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("RefreshToken is not Valid.");
            }

            String userKey = authService.selectUserKeyByToken(refreshToken);

            authService.deleteToken(userKey);
            String newRefreshToken = authService.createRefreshToken(userKey);
            String newAccessToken = authService.jwtTokenGenerator(userKey);

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
