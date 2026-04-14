package com.grape.module.social.adaptor;

import com.grape.module.social.model.SocialLoginCommand;
import com.grape.module.social.model.SocialProfile;
import com.grape.module.social.model.SocialProvider;
import com.grape.module.social.model.SocialTokenResponse;
import com.grape.module.social.model.provider.GoogleUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
public class GoogleOAuthClient implements SocialOAuthClient{

    private final RestTemplate restTemplate;

    @Value("${social.google.client-id}")
    private String clientId;
    @Value("${social.google.client-secret}")
    private String clientSecret;
    @Value("${social.google.redirect-uri}")
    private String redirectUri;
    @Value("${social.google.token-uri}")
    private String tokenUri;
    @Value("${social.google.user-info-uri}")
    private String userInfoUri;

    @Override
    public boolean supports(SocialProvider provider) {
        return SocialProvider.GOOGLE == provider;
    }

    @Override
    public SocialProfile getProfile(SocialLoginCommand command) {
        SocialTokenResponse tokenResponse = requestAccessToken(command.getCode());
        GoogleUserResponse userResponse = requestUserInfo(tokenResponse.getAccessToken());
        return toSocialProfile(userResponse);
    }

    private SocialTokenResponse requestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<SocialTokenResponse> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                requestEntity,
                SocialTokenResponse.class
        );

        SocialTokenResponse responseBody = response.getBody();
        if(responseBody == null || responseBody.getAccessToken() == null) {
            throw new IllegalStateException("Google access token request failed");
        }

        return responseBody;
    }

    private GoogleUserResponse requestUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserResponse> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                requestEntity,
                GoogleUserResponse.class
        );

        GoogleUserResponse responseBody = response.getBody();
        if(responseBody == null || responseBody.getSub() == null) {
            throw new IllegalStateException("Google user info select failed");
        }

        return responseBody;
    }

    private SocialProfile toSocialProfile(GoogleUserResponse userResponse) {
        return SocialProfile.builder()
                .provider(SocialProvider.GOOGLE)
                .providerUserId(userResponse.getSub())
                .socialUserId(userResponse.getEmail())
                .email(userResponse.getEmail())
                .name(userResponse.getName())
                .nickname(userResponse.getName())
                .build();
    }
}
