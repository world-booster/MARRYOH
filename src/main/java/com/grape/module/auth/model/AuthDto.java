package com.grape.module.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto {

    @NotBlank
    private String tokenKey;
    @NotBlank
    private String userKey;
    @NotBlank
    private String token;
    @NotBlank
    private LocalDateTime tokenStartDt;
    private LocalDateTime tokenEndDt;
}
