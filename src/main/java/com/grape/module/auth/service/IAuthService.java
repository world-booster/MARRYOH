package com.grape.module.auth.service;

import io.jsonwebtoken.Claims;

import java.security.PrivateKey;

public interface IAuthService {

    /*
     * 인증 관련 서비스 로직
     * **********[타입별 반환 값]*****************
     * 1. int : return 1(성공) or 이외 실패
     * 2. boolean : return 1(성공) or 0(실패)
     * ****************[추가]*******************
     * 값 검증 로직 포함(글자 수, 제한 조건, 필수 조건)
     *  */

    int createRefreshToken(String userKey);
    String selectRefreshToken(String userKey);
    String createTempToken(String userKey);
    String jwtTokenGenerator(String userKey);
    int checkUserToken(String userKey);
    Claims parseJwtToken(String token);
    boolean verifyAccessToken(String token);
    boolean verifyTempToken(String token);
    int deleteToken(String userKey);
}
