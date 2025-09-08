package com.grape.user.service;

import com.grape.user.model.UserDto;
import com.grape.user.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final IUserRepository  userRepository;

    @Override
    public int signup(UserDto user) {
        return 0;
    }

    @Override
    public boolean isUserIdExist(String userId) {
        return userRepository.selectUserId(userId);
    }

    @Override
    public boolean isUserNicknameExist(String userNickname) {
        return userRepository.selectUserNickname(userNickname);
    }

    @Override
    public boolean isUserEmailExist(String userEmail) {
        return userRepository.selectUserEmail(userEmail);
    }


}
