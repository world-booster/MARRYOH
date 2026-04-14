package com.grape.module.user.adaptor;

public interface UserAuthPort {
        String createRefreshToken(String userKey);
        String jwtTokenGenerator(String userKey);
        int deleteTokenByToken(String refreshToken);
}
