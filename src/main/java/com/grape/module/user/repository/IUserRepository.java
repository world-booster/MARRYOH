package com.grape.module.user.repository;

import com.grape.module.user.model.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface IUserRepository {

    /*
    * 회원 정보 관련 메서드 입니다.
    * **********[타입별 반환 값]*****************
    * 1. int : return 1(성공) or 이외 (실패)
    * 2. boolean : return 1(성공) or 0 (실패)
    * */
    int insertUser(UserDto user);
    String selectUserPw(String userId);
    boolean existUserId(String userId);
    boolean existUserNickname(String userNickname);
    boolean existUserEmail(String userEmail);
    boolean existUserKey(String userKey);
    Optional<UserDto> selectUser(@Param("userId") String userId);
    Optional<String> selectUserId(@Param("email") String email);
    Optional<UserDto> selectUserByKey(String userKey);
    int updateUserPassword(UserDto user);
    int updateUserProfile(UserDto user);
}
