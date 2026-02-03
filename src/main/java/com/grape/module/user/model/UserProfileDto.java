package com.grape.module.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String userId;
    private String userNickname;
    private String userEmail;
    private String signupDt;
    private String loginAuth;
    private String adminYnl;
}
