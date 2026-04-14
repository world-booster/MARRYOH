package com.grape.module.social.adaptor;

public interface SocialAuthPort {
    String createAccessToken(String userKey);
    String createRefreshToken(String userKey);
}
