package com.grape.module.social.service;

import com.grape.module.social.model.SocialProfile;
import com.grape.module.user.adaptor.UserPort;
import com.grape.module.user.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialSignupService {

    private static final Logger logger = LogManager.getLogger(SocialSignupService.class);

    private final UserPort userPort;
    private final SocialAccountService socialAccountService;

    @Transactional
    public String signup(SocialProfile profile) {

        String userId = generateUserId(profile);
        String email = profile.getEmail();
        String nickname = resolveNickname(profile);

        String userKey = userPort.createSocialUser(
                userId,
                email,
                nickname
        );

        socialAccountService.insertSocialAccount(userKey, profile);

        return userKey;
    }

    private String generateUserId(SocialProfile profile) {
        return profile.getProvider().name().toLowerCase() + "_" + UUID.randomUUID().toString().substring(0,8);
    }

    private String resolveNickname(SocialProfile profile) {
        if (profile.getNickname() != null && !profile.getNickname().isBlank()) {
            return profile.getNickname();
        }

        if (profile.getName() != null && !profile.getName().isBlank()) {
            return profile.getName();
        }

        return profile.getProvider().name().toLowerCase() + "_user";
    }
}
