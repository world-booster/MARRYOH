package com.grape.user.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
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

    private String userKey;
    @Size(min = 1, max = 20, message = "아이디는 20자 이내로 입력하세요.")
    @Pattern(regexp = "^(?!.*설사)(?!.*방구)[a-zA-Z0-9!@#\\$%\\^&\\*\\(\\)_]*$", message = "잘못된 언어 형식 입니다.")
    private String userId;
    @Size(min = 7, max = 20, message = "비밀번호는 7자 이상 20자 이내로 입력하세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z0-9])(?=.*[!@#\\$%\\^&\\*\\(\\)_])[a-zA-Z0-9!@#\\$%\\^&\\*\\(\\)_]*$",
            message = "비밀번호에는 영문, 숫자, 특수기호(!,@,#,$,%,^,&,*,(,),_ 중 하나가 꼭 포함되어야 합니다.")
    private String userPw;
    private String userEmail;
    @Size(min = 1, max = 50, message = "이름은 50자 이내로 입력하세요.")
    @Pattern(regexp = "[a-zA-Z가-힣!@#\\$%\\^&\\*]*$", message = "잘못된 언어 형식 입니다.")
    private String userName;
    private String userRRM; //주민등록번호, Resident-Registration-Number
    private String phoneNumber;
    private String socialLoginYn;
    private String socialProvider;
    private String socialProviderKey;
    @Size(min = 1, max = 20, message = "별명은 20자 이내로 입력하세요.")
    @Pattern(regexp = "^(?!.*설사)(?!.*방구)[a-zA-Z0-9가-힣!@#\\$%\\^&\\*\\(\\)_]*$", message = "잘못된 언어 형식 입니다.")
    private String userNickname;
    private int profileSequence;
    private LocalDateTime signupDt;
    private LocalDateTime userInfoUpdateDt;
    private LocalDateTime lastLoginDt;
    private int pwFailCount;
    private String userStatus;
    private LocalDateTime lastStatusChangedDt;
    private String userStatusContent;
    private int loginTryCount;
    private String loginAuth;
    private String adminYn;
    private int warnCount;
    private int banCount;
}
