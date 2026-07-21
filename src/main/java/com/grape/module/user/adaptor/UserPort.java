package com.grape.module.user.adaptor;

import com.grape.module.user.model.UserDto;

import java.util.Optional;

public interface UserPort {
    String selectUserEmail(String userKey);
    boolean existUserKey(String userKey);
    String createSocialUser(String userId, String userEmail, String userNickname);
    Optional<UserDto> selectUser(String userKey);
    String selectUserNickname(String userKey);
}
