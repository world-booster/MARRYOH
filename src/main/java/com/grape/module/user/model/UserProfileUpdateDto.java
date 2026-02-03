package com.grape.module.user.model;

import lombok.Data;

@Data
public class UserProfileUpdateDto {
    private String userNickname;
    private String userEmail;
}
