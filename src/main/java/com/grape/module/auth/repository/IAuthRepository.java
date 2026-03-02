package com.grape.module.auth.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IAuthRepository {

    /*
    * 인증 관련 메서드 입니다.
    * **********[타입별 반환 값]*****************
    * 1. int : return 1(성공) or 이외 (실패)
    * 2. boolean : return 1(성공) or 0 (실패)
    * */

    int insertToken(String token, String tokenKey, String userKey);
    int existUserToken(String userKey);
    int existToken(String token);
    int deleteToken(String userKey);
    int deleteTokenByToken(String token);
    String selectRefreshToken(String userKey);
    String selectUserKeyByToken(String token);

}
