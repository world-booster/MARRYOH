package com.grape.module.social.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SocialProfile {
    private final SocialProvider provider;
    private final String providerUserId;
    private final String socialUserId;
    private final String email;
    private final String name;
    private final String nickname;
}
