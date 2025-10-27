package com.grape.module.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFindingInfoDto {

    private String id;
    private String newPw;
    private String newPwCheck;
    private String tempToken;
    private String refreshToken;
    private String accessToken;
}
