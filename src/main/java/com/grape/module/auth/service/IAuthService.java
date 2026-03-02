package com.grape.module.auth.service;

import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;

public interface IAuthService {

    /*
     * 인증 관련 서비스 로직
     * **********[타입별 반환 값]*****************
     * 1. int : return 1(성공) or 이외 실패
     * 2. boolean : return 1(성공) or 0(실패)
     * ****************[추가]*******************
     * 값 검증 로직 포함(글자 수, 제한 조건, 필수 조건)
     *  */

    /* 토큰 관련 */
    String createRefreshToken(String userKey);
    String selectRefreshToken(String userKey);
    String createTempToken(String userKey);
    String jwtTokenGenerator(String userKey);
    String refreshTokenGenerator(String userKey);
    String hashRefreshToken(String refreshToken);
    int checkUserToken(String userKey);
    int checkToken(String refreshToken);
    Claims parseJwtToken(String token);
    boolean verifyAccessToken(String token);
    boolean verifyTempToken(String token);
    boolean verifyRefreshToken(String token, String userKey);
    int deleteToken(String userKey);
    int deleteTokenByToken(String refreshToken);
    String selectUserKeyByToken(String refreshToken);

    /* 이메일 인증 관련 */
    void sendEmailVerification(String email) throws MessagingException;
    boolean verifyEmailCode(String email, String code);
}
