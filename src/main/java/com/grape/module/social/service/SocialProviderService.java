package com.grape.module.social.service;

import com.grape.module.social.adaptor.SocialOAuthClient;
import com.grape.module.social.model.SocialProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialProviderService {

    private final List<SocialOAuthClient> socialOAuthClients;

    public SocialOAuthClient getClient(SocialProvider provider){
        return socialOAuthClients.stream()
                .filter(client -> client.supports(provider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 제공자"));
    }
}
