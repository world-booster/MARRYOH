package com.grape.module.user.controller;

import com.grape.module.user.model.UserDto;
import com.grape.module.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/*
* 유저 상태 및 정보관련 CRUD 서비스 매핑
* 회원가입, 로그인, 로그아웃, 마이페이지 조회, 회원 정보 수정, 회원 정보 검증
* */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/verificator-id")
    public ResponseEntity<Map<String,String>> verificateId(@RequestBody UserDto user){
        Map<String,String> response = new HashMap<>();
        try {
            if(userService.checkUserInfo(user.getUserId(),"id") == 1){
                response.put("error","이미 존재하거나, 올바르지 않은 ID 입니다.");
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
            if(userService.checkUserInfo(user.getUserNickname(),"nickname") == 1){
                response.put("error","이미 존재하거나, 올바르지 않은 별명 입니다.");
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
            if (userService.checkUserInfo(user.getUserEmail(),"email") == 1) {
                response.put("error","이미 존재하는 이메일 입니다.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            //이메일 인증 들어가야함

            if (userService.signup(user) != 1) {
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
}
