package com.grape.module.social.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialAccount {

    private Long socialAccountKey;
    private String userKey;
    private SocialProvider provider;
    private String providerKey;
    private String socialUserId;
    private String linkStatus;
    private LocalDateTime linkedDt;
    private LocalDateTime lastSocialLoginDt;
    private LocalDateTime createdDt;
    private LocalDateTime updatedDt;
}
