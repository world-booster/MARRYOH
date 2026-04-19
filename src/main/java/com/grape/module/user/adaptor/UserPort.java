package com.grape.module.user.adaptor;

public interface UserPort {
    String selectUserEmail(String userKey);
    boolean existUserKey(String userKey);
    String createSocialUser(String userId, String userEmail, String userNickname);
}
