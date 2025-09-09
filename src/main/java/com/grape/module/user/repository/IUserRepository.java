package com.grape.module.user.repository;

import com.grape.module.user.model.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserRepository {

    /*
    * 회원 정보 관련 메서드 입니다.
    * **********[타입별 반환 값]*****************
    * 1. int : return 1(성공) or 이외 (실패)
    * 2. boolean : return 1(성공) or 0 (실패)
    * */
    int insertUser(UserDto user);
    boolean selectUserId(String userId);
    boolean selectUserNickname(String userNickname);
    boolean selectUserEmail(String userEmail);
    boolean selectUserKey(String userKey);
}
