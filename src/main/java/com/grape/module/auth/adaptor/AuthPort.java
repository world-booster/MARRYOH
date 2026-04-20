package com.grape.module.auth.adaptor;

public interface AuthPort {

    String createRefreshToken(String userKey);
    String createAccessToken(String userKey);
    String jwtTokenGenerator(String userKey);
    int deleteTokenByToken(String refreshToken);

}
