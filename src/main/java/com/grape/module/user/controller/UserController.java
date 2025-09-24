package com.grape.module.user.controller;

import com.grape.module.user.model.UserDto;
import com.grape.module.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

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
    public String verificateId(@RequestBody UserDto user){

        if(userService.isUserIdExist(user.getUserId()) == 1){
            return "fail";
        }else{
            return "success";
        }
    }

    @PostMapping("/verificator-nickname")
    public String verificateNickname(@RequestBody UserDto user){

        if(userService.isUserNicknameExist(user.getUserNickname()) == 1){
            return "fail";
        }else{
            return "success";
        }
    }


    @PostMapping("/signup")
    public String signup(@RequestBody UserDto user){

        if(userService.isUserEmailExist(user.getUserEmail()) == 1){
            return "이미 존재하는 이메일 입니다.";
        }

        //이메일 인증 들어가야함
        if(userService.signup(user) > 0){
            return "success";
        }else{
            return "fail";
        }
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
