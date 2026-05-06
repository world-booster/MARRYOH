package com.grape.module.user.adaptor;

import com.grape.module.user.model.UserDto;
import com.grape.module.user.service.FindUserInfoService;
import com.grape.module.user.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAdaptor implements UserPort {

    private final FindUserInfoService findUserInfoService;
    private final SignupService signupService;

    @Override
    public String selectUserEmail(String userKey){
        return findUserInfoService.selectUserEmail(userKey);
    }

    @Override
    public boolean existUserKey(String userKey){
        return findUserInfoService.existUserKey(userKey);
    }

    @Override
    public String createSocialUser(String userId, String userEmail, String userNickname) {
        return signupService.createSocialUser(userId, userEmail, userNickname);
    }

    @Override
    public Optional<UserDto> selectUser(String userKey) {
        return findUserInfoService.selectUser(userKey);
    }
}
