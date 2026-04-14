package com.grape.module.social.repository;

import com.grape.module.social.model.SocialAccount;
import com.grape.module.social.model.SocialProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface SocialAccountRepository {

    SocialAccount findByProviderAndProviderUserId(@Param("provider")SocialProvider provider,
                                                  @Param("providerUserId") String providerUserId);
    int insertSocialAccount(SocialAccount socialAccount);
    int updateLastSocialLoginDt(@Param("socialAccountKey") Long socialAccountKey,
                                @Param("lastSocialLoginDt")LocalDateTime lastSocialLoginDt);


}
