package com.grape.module.user.service;

import com.grape.config.properties.DefaultUserProperties;
import com.grape.module.auth.adaptor.AuthPort;
import com.grape.module.user.model.UserDto;
import com.grape.module.user.repository.IUserRepository;
import com.grape.utils.DateFormatter;
import com.grape.utils.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class SignupService {

    private static final Logger logger = LogManager.getLogger(SignupService.class);

    private final IUserRepository userRepository;
    private final DateFormatter dateFormatter;
    private final Validator validator;
    private final PasswordEncoder passwordEncoder;
    private final DefaultUserProperties defaultUserProperties;
    private final AuthPort authPort;

    /* Regist user-info of common */
    public int signup(UserDto user){
        user.setUserKey(generateUuidV7());
        user.setSignupDt(dateFormatter.getMariaDbDateTime());
        user.setLoginAuth(defaultUserProperties.loginAuth());
        user.setAdminYn(defaultUserProperties.adminYn());

        if(!validator.isValidId(user.getUserId())){
            return 0;
        }
        if(!validator.isValidNickname(user.getUserNickname())){
            return 0;
        }
        if(!validator.isValidPassword(user.getUserPw())){
            return 0;
        }
        if(userRepository.existUserEmail(user.getUserKey())){
            return 0;
        }

        String encodedPw = passwordEncoder.encode(user.getUserPw());
        user.setUserPw(encodedPw);

        int result = userRepository.insertUser(user);
        if(result == 0) return 0;

        String refreshToken = authPort.createRefreshToken(user.getUserKey());
        if(refreshToken == null) return 0;

        return result;
    }

    @Transactional
    public String createSocialUser(String userId, String userEmail, String userNickname) {
        String userKey = generateUuidV7();

        UserDto user = UserDto.builder()
                .userKey(userKey)
                .signupDt(dateFormatter.getMariaDbDateTime())
                .loginAuth("S")
                .adminYn(defaultUserProperties.adminYn())
                .userId(userId)
                .userNickname(userNickname)
                .userEmail(userEmail)
                .userPw("temp1234!") // temp for dev
                .build();

        int result = userRepository.insertUser(user);
        if(result == 0) return null;

        String refreshToken = authPort.createRefreshToken(user.getUserKey());
        if(refreshToken == null) return null;

        return userKey;
    }

    private String generateUuidV7(){
        // 무한 루프 방지 + JDK UUID 사용
        for(int i = 0; i < 10; i++) {
            String uuidV7 = UUID.randomUUID().toString().replace("-", "");
            if(!userRepository.existUserKey(uuidV7)) {
                return uuidV7;
            }
        }
        logger.debug("userKey 생성 실패 - 10회 시도 모두 중복");
        throw new RuntimeException("고유한 userKey 생성 실패");
    }

}
