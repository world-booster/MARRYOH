package com.grape.module.user.service;

import com.grape.module.auth.adaptor.AuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignoutService {

    private final AuthPort authPort;

    public void signout(String refreshToken){
        if(refreshToken == null) {
            return;
        }
        authPort.deleteTokenByToken(refreshToken);
    }
}
