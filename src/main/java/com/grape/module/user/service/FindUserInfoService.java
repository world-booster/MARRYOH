package com.grape.module.user.service;

import com.grape.module.user.exception.UserErrorCode;
import com.grape.module.user.exception.UserException;
import com.grape.module.user.model.FindIdDto;
import com.grape.module.user.model.ResetPwDto;
import com.grape.module.user.model.UserDto;
import com.grape.module.user.repository.IUserRepository;
import com.grape.utils.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindUserInfoService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public String findId(FindIdDto findIdDto){

        if(!userRepository.existUserEmail(findIdDto.getEmail())){
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }

        String userId = userRepository.selectUserId(findIdDto.getEmail())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return userId.substring(0,4) + "*".repeat(userId.length() - 4);
    }

    public int resetPassword(ResetPwDto resetPwDto){
        if(!resetPwDto.isVerified()){
            return 0;
        }

        if(!validator.isValidPassword(resetPwDto.getNewPassword())){
            return 0;
        }

        UserDto user = userRepository.selectUser(resetPwDto.getUserId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        String encodedPw = passwordEncoder.encode(resetPwDto.getNewPassword());
        user.setUserPw(encodedPw);
        return userRepository.updateUserPassword(user);
    }

    public boolean existUserId(String userId){
        if(userId == null){
            return true;
        }
        if(!validator.isValidId(userId)){
            return true;
        }
        return userRepository.existUserId(userId);
    }

    public boolean existUserNickname(String nickname){
        if(nickname == null){
            return true;
        }
        if(!validator.isValidNickname(nickname)){
            return true;
        }
        return userRepository.existUserNickname(nickname);
    }

    public boolean existUserKey(String userKey){
        if(userKey == null){
         return false;
        }
        return userRepository.existUserKey(userKey);
    }

    public String selectUserEmail(String userKey){
        if(userKey == null) {
            return null;
        }
        return userRepository.selectUserEmail(userKey);
    }
}
