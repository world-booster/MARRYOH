package com.grape.module.auth.controller;

import com.grape.module.auth.service.IAuthService;
import com.grape.module.user.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    private final IAuthService authService;

    @PostMapping("/token-verify")
    public ResponseEntity<String> tokenVerify(
            @RequestHeader("Authorization") String header,
            @RequestBody Map<String, String> request){
        try{
            String token = header.replace("Bearer","");
            String tokenType = request.get("tokenType");
            logger.debug("토큰 타입:"+tokenType);
            logger.debug("토큰 길이:"+token.length());

            boolean isValid;
            if("TEMP".equals(tokenType)){
                isValid = authService.verifyTempToken(token);
            }else if("ACCESS".equals(tokenType)){
                isValid = authService.verifyAccessToken(token);
            } else if("REFRESH".equals(tokenType)){
                isValid = authService.verifyRefreshToken(token);
            } else {
                return ResponseEntity.badRequest().body("알 수 없는 토큰 타입");
            }

            return isValid ? ResponseEntity.ok("ok") : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 검증 실패");
        } catch (Exception e){
            logger.debug("토큰 검증 에러:"+e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 인증 - 서버 오류");
        }
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
