package com.grape.module.auth.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "security.email")
public record SecurityEmailProperties (
    String pepper,
    Duration ttl,
    Duration cooldown,
    int maxAttempts,
    Duration lockTime
) {
    public SecurityEmailProperties{
        if (pepper == null) pepper = "";
        if (ttl == null) ttl = Duration.ofMinutes(5);
        if (cooldown == null) cooldown = Duration.ofSeconds(10);
        if (maxAttempts <= 0) maxAttempts = 5;
        if (lockTime == null) lockTime = Duration.ofMinutes(10);
    }
}