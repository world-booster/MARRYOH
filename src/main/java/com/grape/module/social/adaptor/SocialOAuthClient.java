package com.grape.module.social.adaptor;

import com.grape.module.social.model.SocialLoginCommand;
import com.grape.module.social.model.SocialProfile;
import com.grape.module.social.model.SocialProvider;

public interface SocialOAuthClient {
    boolean supports(SocialProvider provider);
    SocialProfile getProfile(SocialLoginCommand command);
}
