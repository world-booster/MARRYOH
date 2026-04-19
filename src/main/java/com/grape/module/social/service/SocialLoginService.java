package com.grape.module.social.service;

import com.grape.module.auth.adaptor.AuthPort;
import com.grape.module.social.adaptor.SocialOAuthClient;
import com.grape.module.social.model.SocialAccount;
import com.grape.module.social.model.SocialLoginCommand;
import com.grape.module.social.model.SocialLoginResult;
import com.grape.module.social.model.SocialProfile;
import com.grape.module.user.adaptor.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final SocialProviderService socialProviderService;
    private final SocialAccountService socialAccountService;
    private final SocialSignupService socialSignupService;
    private final AuthPort authPort;
    private final UserPort userPort;

    @Transactional
    public SocialLoginResult login(SocialLoginCommand command){
        SocialOAuthClient client = socialProviderService.getClient(command.getProvider());
        SocialProfile profile = client.getProfile(command);

        SocialAccount socialAccount = socialAccountService.findByProviderAndProviderUserId(
                profile.getProvider(),
                profile.getProviderUserId()
        );

        String userKey;
        String userEmail;
        boolean newUser = false;

        if(socialAccount == null){
            userKey = socialSignupService.signup(profile);
            newUser = true;
        } else {
            userKey = socialAccount.getUserKey();

            socialAccountService.updateLastLogin(socialAccount.getSocialAccountKey());
        }
        userEmail = userPort.selectUserEmail(userKey);
        String refreshToken = authPort.createRefreshToken(userKey);
        String accessToken = authPort.createAccessToken(userKey);

        return SocialLoginResult.builder()
                .userEmail(userEmail)
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .newUser(newUser)
                .build();
    }

}
