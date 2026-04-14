package com.grape.module.social.adaptor;

import com.grape.module.social.model.SocialLoginCommand;
import com.grape.module.social.model.SocialProfile;
import com.grape.module.social.model.SocialProvider;
import com.grape.module.social.model.SocialTokenResponse;
import com.grape.module.social.model.provider.NaverUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NaverOAuthClient implements SocialOAuthClient {

    private final RestTemplate restTemplate;

    @Value("${social.naver.client-id}")
    private String clientId;

    @Value("${social.naver.client-secret}")
    private String clientSecret;

    @Value("${social.naver.token-uri}")
    private String tokenUri;

    @Value("${social.naver.user-info-uri}")
    private String userInfoUri;

    @Override
    public boolean supports(SocialProvider provider) {
        return SocialProvider.NAVER == provider;
    }

    @Override
    public SocialProfile getProfile(SocialLoginCommand command) {
        SocialTokenResponse tokenResponse = requestAccessToken(command.getCode(), command.getState());
        NaverUserResponse userResponse = requestUserInfo(tokenResponse.getAccessToken());
        return toSocialProfile(userResponse);
    }

    private SocialTokenResponse requestAccessToken(String code, String state) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("state", state);

        HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<SocialTokenResponse> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                requestEntity,
                SocialTokenResponse.class
        );

        SocialTokenResponse responseBody = response.getBody();
        if(responseBody == null || responseBody.getAccessToken() == null) {
            throw new IllegalStateException("Naver access token request failed");
        }

        return responseBody;
    }

    private NaverUserResponse requestUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<NaverUserResponse> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                requestEntity,
                NaverUserResponse.class
        );

        NaverUserResponse responseBody = response.getBody();
        if(responseBody == null
            || responseBody.getResponse() == null
            || responseBody.getResponse().getId() == null){
            throw new IllegalStateException("Naver user info select failed");
        }

        return responseBody;
    }

    private SocialProfile toSocialProfile(NaverUserResponse userResponse){
        NaverUserResponse.Response response = userResponse.getResponse();

        return SocialProfile.builder()
                .provider(SocialProvider.NAVER)
                .providerUserId(response.getId())
                .socialUserId(response.getEmail() != null ? response.getEmail() : response.getId())
                .email(response.getEmail())
                .name(response.getName())
                .nickname(response.getNickname())
                .build();
    }
}
