package com.grape.module.social.service;

import com.grape.module.social.model.SocialProfile;
import com.grape.module.social.model.SocialProvider;
import com.grape.module.social.model.SocialSignupRequest;
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

    private final SignupService signupService; // will be updated that use port && adaptor structure
    private final SocialAccountService socialAccountService;

    @Transactional
    public String signup(SocialProfile profile) {

        String userId = generateUserId(profile);
        String email = profile.getEmail();
        String nickname = resolveNickname(profile);

        logger.debug("social signup start userId ={}, email={}, nickname={}",userId,email,nickname);

        String userKey = signupService.createSocialUser(
                userId,
                email,
                nickname
        );
        logger.debug("createSocialUser success userKey={}", userKey);
        socialAccountService.insertSocialAccount(userKey, profile);
        logger.debug("insertSocialAccount success userKey={}", userKey);
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
