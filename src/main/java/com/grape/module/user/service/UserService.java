package com.grape.module.user.service;

import com.grape.common.DateFormat;
import com.grape.common.Validator;
import com.grape.module.user.model.UserDto;
import com.grape.module.user.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final IUserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DateFormat dateFormat;
    private final Validator validator;

    @Override
    public int signup(UserDto user) {

        /* 유저키 생성 */
        String userKey = UUID.randomUUID().toString();
        while (isUserKeyExist(userKey) == 1) {
            userKey = UUID.randomUUID().toString();
        }
        user.setUserKey(userKey);
        /* 회원정보 미입력 검증 및 초기화 */
        if(user.getUserId() == null){
            return 0;
        } else if (user.getUserPw() == null) {
            return 0;
        } else if (user.getUserEmail() == null) {
            return 0;
        } else if (user.getUserNickname() == null) {
            return 0;
        } else if (user.getSocialLoginYn() == null) {
            user.setSocialLoginYn("N");
        } else if (user.getSignupDt() == null) {
            user.setSignupDt(dateFormat.getNow());
        } else if (user.getLoginAuth() == null) {
            user.setLoginAuth("L");
        } else if (!user.getAdminYn().equals("Y")) {
            user.setAdminYn("N");
        }

        /* 입력정보 유효성 검증 */
        if(!validator.isTextValid(user.getUserId(),5,20,false, false, false, false)) return 1;
        if(!validator.isTextValid(user.getUserPw(), 7, 10, true, true, true, true)) return 1;
        if(!validator.isTextValid(user.getUserNickname(), 2, 10, false, false, false, false)) return 1;

        /* 비밀번호 bcrypt 암호화 */
        String encodedPw = passwordEncoder.encode(user.getUserPw());
        user.setUserPw(encodedPw);

        return userRepository.insertUser(user);
    }

    @Override
    public int isUserIdExist(String userId) {
        return userRepository.selectUserId(userId);
    }

    @Override
    public int isUserNicknameExist(String userNickname) {
        return userRepository.selectUserNickname(userNickname);
    }

    @Override
    public int isUserEmailExist(String userEmail) {
        return userRepository.selectUserEmail(userEmail);
    }

    @Override
    public int isUserKeyExist(String userKey) {
        return userRepository.selectUserKey(userKey);
    }


}
