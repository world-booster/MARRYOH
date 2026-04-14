package com.grape.module.social.adaptor;

import com.grape.module.social.model.SocialProfile;
import com.grape.module.social.model.SocialProvider;

public interface SocialProviderPort {

    boolean supports(SocialProvider provider);
    SocialProfile getSocialProfile(String code, String state);
}
