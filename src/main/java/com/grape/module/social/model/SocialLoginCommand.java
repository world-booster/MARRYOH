package com.grape.module.social.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SocialLoginCommand {

    private final SocialProvider provider;
    private final String code;
    private final String state;
}
