package com.grape.user.service;

import com.grape.user.model.UserDto;

public interface IUserService {

    /*
    * 회원 정보 관련 서비스 로직
    * **********[타입별 반환 값]*****************
    * 1. int : return 1(성공) or 이외 실패
    * 2. boolean : return 1(성공) or 0(실패)
    * ****************[추가]*******************
    * 값 검증 로직 포함(글자 수, 제한 조건, 필수 조건)
    *  */
    int signup(UserDto user);
    boolean isUserIdExist(String userId);
    boolean isUserNicknameExist(String userNickname);
    boolean isUserEmailExist(String userEmail);
}
