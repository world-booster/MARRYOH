package com.grape.module.social.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialSignupRequest {
    private String userId;
    private String userNickname;
    private String userEmail;
    private String provider;
    private String socialUserId;
    private String providerUserId;
}
