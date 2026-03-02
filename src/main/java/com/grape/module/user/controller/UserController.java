package com.grape.module.user.controller;

import com.grape.module.auth.model.TokenDto;
import com.grape.module.user.model.*;
import com.grape.module.user.service.SignupService;
import com.grape.module.user.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/*
* 유저 상태 및 정보관련 CRUD 서비스 매핑
* 회원가입, 로그인, 로그아웃, 마이페이지 조회, 회원 정보 수정, 회원 정보 검증
* */
@CrossOrigin(origins = "http://localhost:*")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SignupService signupService;

    @PostMapping("/verificator-id")
    public ResponseEntity<Map<String,String>> verificateId(@RequestBody UserDto user){
        Map<String,String> response = new HashMap<>();
        try {
            if(userService.checkUserInfo(user.getUserId(),"id")){
                response.put("error","이미 존재하는 ID 입니다.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }else{
                response.put("message","success");
                return ResponseEntity.ok(response);
            }
        }catch (HttpMessageNotReadableException e){
            response.put("error", "요청 파일 read 에러");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (Exception e){
            response.put("error", "원인 미지정 에러");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/verificator-nickname")
    public ResponseEntity<Map<String,String>> verificateNickname(@RequestBody UserDto user){
        Map<String,String> response = new HashMap<>();
        try{
            if(userService.checkUserInfo(user.getUserNickname(),"nickname")){
                response.put("error","이미 존재하는 별명입니다.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }else{
                response.put("error", "요청 파일 read 에러");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }catch (HttpMessageNotReadableException e){
            response.put("error", "요청 파일 read 에러");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (Exception e){
            response.put("error", "원인 미지정 에러");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<Map<String,String>> signup(@RequestBody UserDto user){
        Map<String,String> response = new HashMap<>();
        try {
            if (signupService.signup(user) != 1) {
                response.put("error", "가입 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }catch (HttpMessageNotReadableException e){
            response.put("error", "요청 파일 read 에러");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (Exception e){
            response.put("error", "에러: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message","success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/find-id")
    public ResponseEntity<Map<String,String>> findId(@RequestBody FindIdDto findIdDto){
        Map<String,String> response = new HashMap<>();
        try{
            String userId = userService.findId(findIdDto);
            String maskedUserId = userId.substring(0,4) + "*".repeat(userId.length() - 4);
            response.put("maskedUserId",maskedUserId);
        }catch (Exception e){
            response.put("error", "원인 미지정 에러");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-pw")
    public ResponseEntity<Map<String,String>> findPw(@RequestBody ResetPwDto resetPwDto){
        Map<String,String> response = new HashMap<>();
        try {
            if(userService.resetPassword(resetPwDto) != 1){
                response.put("error", "가입 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }catch (HttpMessageNotReadableException e){
            response.put("error", "요청 파일 read 에러");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (Exception e){
            response.put("error", "원인 미지정 에러");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message","success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String,String>> signin(@RequestBody LoginDto loginDto){
        Map<String,String> response = new HashMap<>();
        try{
            TokenDto tokenDto = userService.signin(loginDto);

            ResponseCookie cookie = ResponseCookie.from("REFRESH-TOKEN", tokenDto.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7*24*60*60)
                    .sameSite("Lax")
                    .build();


            response.put("accessToken", tokenDto.getAccessToken());
            response.put("message", "success");
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
        }catch (IllegalArgumentException e) {
            response.put("error", "비밀번호 불일치 또는 사용자 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }catch (RuntimeException e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }catch (Exception e){
            response.put("error", "원인 미지정 에러");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<Map<String,String>> signout(@CookieValue(value = "REFRESH-TOKEN", required = false) String refreshToken){
        Map<String,String> response = new HashMap<>();
        System.out.println("토큰: "+ refreshToken);
        try{
            userService.signout(refreshToken);

            ResponseCookie expiredCookie = ResponseCookie.from("REFRESH-TOKEN", "")
                    .path("/")
                    .httpOnly(true)
                    .maxAge(0)
                    .sameSite("Lax") // 개발단계 : Lax , 운영 : strict
                    .secure(false) // 개발단계 : false , 운영 : true
                    .build();

            response.put("message", "success");
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                    .body(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (RuntimeException e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }catch (Exception e){
            response.put("error", "원인 미지정 에러");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token){
        Map<String,String> response = new HashMap<>();
        try{
            String accessToken = token.replace("Bearer","").trim();
            UserProfileDto profileDto = userService.getProfile(accessToken);
            return ResponseEntity.ok(profileDto);
        } catch (IllegalArgumentException iae){
            response.put("error", iae.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("error", "원인 미지정 에러");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String,String>> updateProfile(
            @RequestHeader("Autorization") String token,
            @RequestBody UserProfileUpdateDto updateDto){
        Map<String, String> response = new HashMap<>();

        try{
            String accessToken = token.replace("Bearer","").trim();
            userService.updateProfile(accessToken, updateDto);
            response.put("message", "success");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("error", "원인 미지정 에러");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test")
    public void test(){
        System.out.println("성공입니다~");
    }
}
