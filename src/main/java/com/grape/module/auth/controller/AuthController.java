package com.grape.module.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grape.module.auth.service.TokenService;
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

    private final TokenService tokenService;
    private final ObjectMapper objectMapper; //임시 추후 DTO 형식으로 설계

    @PostMapping("/update-token")
    public ResponseEntity<?> updateToken(@CookieValue(value = "REFRESH-TOKEN", required = false) String refreshToken) {
        try{
            Claims claims = tokenService.parseJwtToken(refreshToken);

            if(claims.getExpiration().before(new Date())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 만료");
            }

            String userKey = claims.getSubject();

            String storedHash = tokenService.selectRefreshToken(userKey);
            if(storedHash == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 없음");
            }

//            if(!authService.verifyRefreshToken(refreshToken, userKey)){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 불일치");
//            }

//            int newRefreshTokenResult = authService.createRefreshToken(userKey);
//
//            if(newRefreshTokenResult == 0){
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰 생성 실패");
//            }

            String newAccessToken = tokenService.jwtTokenGenerator(userKey);
            String newRefreshToken = tokenService.selectRefreshToken(userKey);

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

            tokenService.sendEmailVerification(email);
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
        if (tokenService.verifyEmailCode(email, code)) {
            response.put("message", "이메일 인증 완료");
            return ResponseEntity.ok(response);
        }
        response.put("error", "인증 실패 또는 만료된 코드입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> reCreateAccessToken(@CookieValue(value = "REFRESH-TOKEN", required = false) String refreshToken){

        try{
            if(refreshToken == null || refreshToken.isBlank()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh 토큰이 존재 하지 않습니다.");
            }

            int checkRefreshToken = tokenService.checkToken(refreshToken);
            System.out.println("check DB: "+checkRefreshToken);
            System.out.println("refreshToken: "+refreshToken);
            String hashedToken = tokenService.hashRefreshToken(refreshToken);
            String userKey = tokenService.selectUserKeyByToken(hashedToken);

            if(userKey == null || userKey.isEmpty()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh 토큰입니다.");
            }

            tokenService.deleteToken(userKey);
            String newRefreshToken = tokenService.createRefreshToken(userKey);
            String newAccessToken = tokenService.jwtTokenGenerator(userKey);

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
