package com.grape.security.auth;

import com.grape.module.user.adaptor.UserPort;
import com.grape.module.user.exception.UserErrorCode;
import com.grape.module.user.exception.UserException;
import com.grape.module.user.model.UserDto;
import com.grape.security.model.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserPort userPort;

    @Override
    public UserDetails loadUserByUsername(String userKey) throws UsernameNotFoundException {

        UserDto userDto = userPort.selectUser(userKey)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        UserInfo userInfo = new UserInfo(
                userDto.getUserKey(),
                userDto.getUserEmail(),
                convertAuthType(userDto.getLoginAuth())
        );

        return new CustomUserPrincipal(
                userInfo.getUserKey(),
                userInfo.getUserEmail(),
                userInfo.getLoginAuth(),
                List.of()
        );
    }

    private String convertAuthType(String loginAuth) {
        return switch (loginAuth) {
            case "S" -> "SOCIAL";
            case "L" -> "NORMAL";
            default -> "UNKNOWNl";
        };
    }
}
