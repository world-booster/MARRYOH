package com.grape.module.social.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SocialLoginResult {
    private final String userEmail;
    private final String accessToken;
    private final String refreshToken;
    private final boolean newUser;
}
