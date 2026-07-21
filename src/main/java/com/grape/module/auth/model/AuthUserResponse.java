package com.grape.module.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUserResponse {

    private String userKey;
    private String email;
    private String loginAuth;
}
