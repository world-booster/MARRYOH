package com.grape.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "user.default")
public record DefaultUserProperties(
        String loginAuth,
        String adminYn,
        String socialLoginYn
) {}
