package com.grape.module.social.model;

public enum SocialProvider {
    GOOGLE,
    KAKAO,
    NAVER;

    public static SocialProvider from(String value) {
        return SocialProvider.valueOf(value.toUpperCase());
    }
}
