package com.grape.module.user.InOut;

public interface AuthTokenPort {
        String createRefreshToken(String userKey);
}
