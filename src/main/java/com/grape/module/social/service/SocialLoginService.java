package com.grape.module.social.service;

import com.grape.module.social.adaptor.SocialAuthPort;
import com.grape.module.social.adaptor.SocialOAuthClient;
import com.grape.module.social.adaptor.SocialUserPort;
import com.grape.module.social.model.SocialAccount;
import com.grape.module.social.model.SocialLoginCommand;
import com.grape.module.social.model.SocialLoginResult;
import com.grape.module.social.model.SocialProfile;
import com.grape.module.user.repository.IUserRepository;
import com.grape.module.user.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final SocialProviderService socialProviderService;
    private final SocialAccountService socialAccountService;
    private final SocialAuthPort socialAuthPort;
    private final SocialSignupService socialSignupService;
    private final IUserRepository userRepository;

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
        userEmail = userRepository.selectUserEmail(userKey);
        String refreshToken = socialAuthPort.createRefreshToken(userKey);
        String accessToken = socialAuthPort.createAccessToken(userKey);

        return SocialLoginResult.builder()
                .userEmail(userEmail)
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .newUser(newUser)
                .build();
    }

}
