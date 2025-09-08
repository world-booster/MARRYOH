package com.grape.user.controller;

import com.grape.user.model.UserDto;
import com.grape.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        if(userService.isUserIdExist(user.getUserId())){
            return "fail";
        }else{
            return "success";
        }
    }

    @PostMapping("/verificator-id")
    public String verificateNickname(@RequestBody UserDto user){

        if(userService.isUserNicknameExist(user.getUserNickname())){
            return "fail";
        }else{
            return "success";
        }
    }


    @PostMapping("/signup")
    public String signup(@RequestBody UserDto user){

        if(userService.isUserEmailExist(user.getUserEmail())){
            return "이미 존재하는 이메일 입니다.";
        }

        //이메일 인증 들어가야함
        if(userService.signup(user) > 0){
            return "success";
        }else{
            return "fail";
        }
    }
}
