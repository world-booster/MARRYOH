package com.grape.module.social.adaptor;

import com.grape.module.social.model.SocialLoginCommand;
import com.grape.module.social.model.SocialProfile;
import com.grape.module.social.model.SocialProvider;
import com.grape.module.social.model.SocialTokenResponse;
import com.grape.module.social.model.provider.KakaoUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoOAuthClient implements SocialOAuthClient {

    private final RestTemplate restTemplate;

    @Value("${social.kakao.client-id}")
    private String clientId;
    @Value("${social.kakao.client-secret}")
    private String clientSecret;
    @Value("${social.kakao.redirect-uri}")
    private String redirectUri;
    @Value("${social.kakao.token-uri}")
    private String tokenUri;
    @Value("${social.kakao.user-info-uri}")
    private String userInfoUri;

    @Override
    public boolean supports(SocialProvider provider) {
        return SocialProvider.KAKAO == provider;
    }

    @Override
    public SocialProfile getProfile(SocialLoginCommand command) {

        SocialTokenResponse tokenResponse = requestAccessToken(command.getCode());

        KakaoUserResponse userResponse = requestUserInfo(tokenResponse.getAccessToken());

        return toSocialProfile(userResponse);
    }

    private SocialTokenResponse requestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        if (clientSecret != null && !clientSecret.isBlank()) {
            body.add("client_secret", clientSecret);
        }

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<SocialTokenResponse> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                requestEntity,
                SocialTokenResponse.class
        );

        SocialTokenResponse responseBody = response.getBody();
        if (responseBody == null || responseBody.getAccessToken() == null) {
            throw new IllegalStateException("Kakao access token 요청에 실패했습니다.");
        }

        return responseBody;
    }

    private KakaoUserResponse requestUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                requestEntity,
                KakaoUserResponse.class
        );

        KakaoUserResponse responseBody = response.getBody();
        if (responseBody == null || responseBody.getId() == null) {
            throw new IllegalStateException("Kakao 사용자 정보 조회에 실패했습니다.");
        }

        return responseBody;
    }

    private SocialProfile toSocialProfile(KakaoUserResponse userResponse) {
        String email = extractEmail(userResponse);
        String nickname = extractNickname(userResponse);
        String providerUserId = String.valueOf(userResponse.getId());

        return SocialProfile.builder()
                .provider(SocialProvider.KAKAO)
                .providerUserId(providerUserId)
                .socialUserId(email != null ? email : providerUserId)
                .email(email)
                .name(nickname)
                .nickname(nickname)
                .build();
    }

    private String extractEmail(KakaoUserResponse userResponse) {
//        if(userResponse.getKakaoAccount() == null) {
//            return null;
//        }
//        return userResponse.getKakaoAccount().getEmail();

        if (userResponse.getKakaoAccount() != null &&
                userResponse.getKakaoAccount().getEmail() != null) {
            return userResponse.getKakaoAccount().getEmail();
        }

        // 👉 여기서 fallback
        return generateKakaoEmail(userResponse.getId());
    }

    private String extractNickname(KakaoUserResponse userResponse) {
        if(userResponse.getKakaoAccount() != null
            && userResponse.getKakaoAccount().getProfile() != null
            && userResponse.getKakaoAccount().getProfile().getNickname() != null) {
            return userResponse.getKakaoAccount().getProfile().getNickname();
        }

        if(userResponse.getProperties() != null) {
            return userResponse.getProperties().getNickname();
        }

        return null;
    }

    private String generateKakaoEmail(Long kakaoId) {
        return "kakao_" + kakaoId + "@kakao.local";
    }
}
