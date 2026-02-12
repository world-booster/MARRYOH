package com.grape.module.user.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * 회원 정보를 담는 객체
 * Date Format = "yy-MM-dd HH:mm:ss"
 * */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank
    private String userKey;
    @NotBlank
    private String userId;
    @NotBlank
    private String userPw;
    @NotBlank
    private String userEmail;
    @NotBlank
    private String userName;
    private String userRRM; //주민등록번호, Resident-Registration-Number
    private String phoneNumber;
    @NotBlank
    private String socialLoginYn;
    private String socialProvider;
    private String socialProviderKey;
    @NotBlank
    private String userNickname;
    private int userProfileSeq;
    private LocalDateTime signupDt;
    private LocalDateTime userInfoUpdateDt;
    private LocalDateTime lastLoginDt;
    private int pwFailCount;
    @NotBlank
    private String userStatus;
    private LocalDateTime lastStatusChangedDt;
    private String userStatusContent;
    private int loginTryCount;
    @NotBlank
    private String loginAuth;
    @NotBlank
    private String adminYn;
    private int warnCount;
    private int banCount;
}
