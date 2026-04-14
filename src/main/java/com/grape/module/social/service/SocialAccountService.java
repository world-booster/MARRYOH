package com.grape.module.social.service;

import com.grape.module.social.model.SocialAccount;
import com.grape.module.social.model.SocialProfile;
import com.grape.module.social.model.SocialProvider;
import com.grape.module.social.repository.SocialAccountRepository;
import com.grape.module.user.repository.IUserRepository;
import com.grape.module.user.service.SignupService;
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
    private final IUserRepository userRepository; // after -> change use port && adaptor structure


    public SocialAccount findByProviderAndProviderUserId(SocialProvider provider, String providerUserId){
        return socialAccountRepository.findByProviderAndProviderUserId(provider, providerUserId);
    }

    public void insertSocialAccount(String userKey, SocialProfile profile){

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
    }

    public void updateLastLogin(Long socialAccountKey){
        socialAccountRepository.updateLastSocialLoginDt(socialAccountKey, LocalDateTime.now());
    }

    private String generateUuidV7(){
        // 무한 루프 방지 + JDK UUID 사용
        for(int i = 0; i < 10; i++) {
            String uuidV7 = UUID.randomUUID().toString().replace("-", "");
            if(!userRepository.existUserKey(uuidV7)) {
                return uuidV7;
            }
        }
        logger.debug("userKey 생성 실패 - 10회 시도 모두 중복");
        throw new RuntimeException("고유한 userKey 생성 실패");
    }
}
