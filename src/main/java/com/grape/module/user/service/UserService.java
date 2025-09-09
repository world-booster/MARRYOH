package com.grape.module.user.service;

import com.grape.common.DateFormat;
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

    @Override
    public int signup(UserDto user) {

        /* 유저키 생성 */
        String userKey = UUID.randomUUID().toString();
        while (!isUserKeyExist(userKey)) {
            userKey = UUID.randomUUID().toString();
        }

        /* 회원관리 미입력 검증 및 회원정보 초기화 */
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

        /* 비밀번호 bcrypt 암호화 */
        String encodedPw = passwordEncoder.encode(user.getUserPw());
        user.setUserPw(encodedPw);

        return userRepository.insertUser(user);
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

    @Override
    public boolean isUserKeyExist(String userKey) {
        return userRepository.selectUserKey(userKey);
    }


}
