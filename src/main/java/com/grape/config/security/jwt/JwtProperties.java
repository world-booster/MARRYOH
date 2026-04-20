package com.grape.config.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    private String privateKeyPath;
    private String publicKeyPath;
    private long accessTokenExpireMillis = 3600000L; // 기본 1시간
    private String issuer = "grape";
    private String accessTokenType = "access";
    private String roleClaimName = "role";
}
