package com.grape.module.user.service;

import com.grape.module.user.adaptor.UserAuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignoutService {

    private final UserAuthPort userAuthPort;

    public void signout(String refreshToken){
        if(refreshToken == null) {
            return;
        }
        userAuthPort.deleteTokenByToken(refreshToken);
    }
}
