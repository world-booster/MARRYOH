package com.grape.module.user.controller;

import com.grape.module.auth.model.TokenDto;
import com.grape.module.user.model.*;
import com.grape.module.user.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * 유저 상태 및 정보관련 CRUD 서비스 매핑
 * 회원가입, 로그인, 로그아웃, 마이페이지 조회, 회원 정보 수정, 회원 정보 검증
 * */
@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final SignupService signupService;
    private final SigninService signinService;
    private final SignoutService signoutService;
    private final FindUserInfoService findUserInfoService;

    @PostMapping("/verificator-id")
    public ResponseEntity<Map<String, String>> verificateId(@RequestBody String userId) {
        Map<String, String> response = new HashMap<>();

        if (findUserInfoService.existUserId(userId)) {
            response.put("error", "유효하지 않거나 중복된 아이디입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message", "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verificator-nickname")
    public ResponseEntity<Map<String, String>> verificateNickname(@RequestBody String nickname) {
        Map<String, String> response = new HashMap<>();
        if (findUserInfoService.existUserNickname(nickname)) {
            response.put("error", "유효하하지 않거나, 중복된 별명입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message", "success");
        return ResponseEntity.ok(response);

    }


    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody UserDto user) {
        Map<String, String> response = new HashMap<>();

        if (signupService.signup(user) != 1) {
            response.put("error", "가입 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/find-id")
    public ResponseEntity<Map<String, String>> findId(@RequestBody FindIdDto findIdDto) {
        Map<String, String> response = new HashMap<>();
        String userId = findUserInfoService.findId(findIdDto);
        if (userId == null) {
            response.put("error", "조건에 해당하는 아이디가 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("maskedUserId", userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-pw")
    public ResponseEntity<Map<String, String>> findPw(@RequestBody ResetPwDto resetPwDto) {
        Map<String, String> response = new HashMap<>();
        if (findUserInfoService.resetPassword(resetPwDto) != 1) {
            response.put("error", "비밀번호 재설정에 실패하였습니다. 시도해 주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> signin(
            @RequestBody LoginDto loginDto,
            HttpServletResponse response
    ) {

        Map<String, String> body = new HashMap<>();

        TokenDto tokenDto = signinService.signin(loginDto);

        Cookie accessCookie = new Cookie(
                "ACCESS-TOKEN",
                tokenDto.getAccessToken()
        );

        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60);

        Cookie refreshCookie = new Cookie(
                "REFRESH-TOKEN",
                tokenDto.getRefreshToken()
        );

        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        body.put("message", "success");

        return ResponseEntity.ok(body);
    }

    @PostMapping("/signout")
    public ResponseEntity<Map<String, String>> signout(
            @CookieValue(
                    value = "REFRESH-TOKEN",
                    required = false
            ) String refreshToken,

            HttpServletResponse response
    ) {

        Map<String, String> body = new HashMap<>();

        signoutService.signout(refreshToken);

        /* ACCESS TOKEN 삭제 */
        Cookie accessCookie = new Cookie(
                "ACCESS-TOKEN",
                ""
        );

        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false); // 운영 HTTPS 환경에서는 true
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);

        /* REFRESH TOKEN 삭제 */
        Cookie refreshCookie = new Cookie(
                "REFRESH-TOKEN",
                ""
        );

        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // 운영 HTTPS 환경에서는 true
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        body.put("message", "success");

        return ResponseEntity.ok(body);
    }
}
