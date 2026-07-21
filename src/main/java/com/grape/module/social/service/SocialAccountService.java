package com.grape.module.social.service;

import com.grape.module.social.exception.SocialErrorCode;
import com.grape.module.social.exception.SocialException;
import com.grape.module.social.model.SocialAccount;
import com.grape.module.social.model.SocialProfile;
import com.grape.module.social.model.SocialProvider;
import com.grape.module.social.repository.SocialAccountRepository;
import com.grape.module.user.adaptor.UserPort;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialAccountService {

    private static final Logger logger = LogManager.getLogger(SocialAccountService.class);

    private final SocialAccountRepository socialAccountRepository;
    private final UserPort userPort;

    public SocialAccount findByProviderAndProviderUserId(SocialProvider provider, String providerUserId){
        return socialAccountRepository.findByProviderAndProviderUserId(provider, providerUserId);
    }

    public void insertSocialAccount(String userKey, SocialProfile profile){

        if(userKey == null || userKey.isBlank()){
            throw new IllegalStateException("[SOCIAL-ACCOUNT-INSERT] userKey is not exist");
        }

        SocialAccount socialAccount = SocialAccount.builder()
                .userKey(userKey)
                .provider(profile.getProvider())
                .providerKey(profile.getProviderUserId())
                .socialUserId(profile.getSocialUserId())
                .linkStatus("LINKED")
                .linkedDt(LocalDateTime.now())
                .lastSocialLoginDt(LocalDateTime.now())
                .createdDt(LocalDateTime.now())
                .updatedDt(LocalDateTime.now())
                .build();

        socialAccountRepository.insertSocialAccount(socialAccount);

        logger.debug("SOCIAL_ACCOUNT_INSERT_SUCCESS userKey={} provider={} providerUserId={}", userKey, profile.getProvider(), profile.getProviderUserId());
    }

    public void updateLastLogin(Long socialAccountKey){
        socialAccountRepository.updateLastSocialLoginDt(socialAccountKey, LocalDateTime.now());

        logger.info("SOCIAL_LOGIN_SUCCESS socialAccountKey={}", socialAccountKey);
    }

    private String generateUuidV7(){
        // 무한 루프 방지 + JDK UUID 사용
        for(int i = 0; i < 10; i++) {
            String uuidV7 = UUID.randomUUID().toString().replace("-", "");
            if(!userPort.existUserKey(uuidV7)) {
                return uuidV7;
            }
        }

        throw new SocialException(
                SocialErrorCode.SIGNUP_FAILED,
                "SIGN_UP_FAILED USER_KEY_GENERATE_FAIL duplicateCount=10"
        );
    }
}
