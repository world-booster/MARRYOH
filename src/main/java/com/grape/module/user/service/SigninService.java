package com.grape.module.user.service;

import com.grape.module.auth.adaptor.AuthPort;
import com.grape.module.auth.model.TokenDto;
import com.grape.module.user.exception.UserErrorCode;
import com.grape.module.user.exception.UserException;
import com.grape.module.user.model.LoginDto;
import com.grape.module.user.model.UserDto;
import com.grape.module.user.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SigninService {

    private static final Logger logger = LogManager.getLogger(SigninService.class);

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthPort authPort;

    public TokenDto signin(LoginDto loginDto) {
        UserDto user = userRepository.selectUser(loginDto.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 아이디로 가입된 회원 정보가 존재하지 않습니다."));

        String encodedPassword = userRepository.selectUserPw(user.getUserId());
        if (encodedPassword == null) {
            throw new UserException(UserErrorCode.EMAIL_DUPLICATED);
        }

        if(!passwordEncoder.matches(loginDto.getPassword(), encodedPassword)){
            throw new UserException(UserErrorCode.PASSWORD_MISMATCH);
        }

        Optional<UserDto> userInfo = userRepository.selectUser(loginDto.getUserId());
        String userKey = userInfo.get().getUserKey();

        String refreshToken = authPort.createRefreshToken(userKey);
        String accessToken = authPort.jwtTokenGenerator(userKey);

        return new TokenDto(accessToken, refreshToken);
    }

}
