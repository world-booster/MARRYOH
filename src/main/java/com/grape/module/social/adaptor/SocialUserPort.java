package com.grape.module.social.adaptor;

import com.grape.module.social.model.SocialProfile;
import com.grape.module.social.model.SocialProvider;

public interface SocialUserPort {

    String findUserKeyBySocialAccount(SocialProvider provider, String providerUserId);
    String createSocialUser(SocialProfile profile);
    void linkSocialAccount(String userKey, SocialProfile profile);
}
