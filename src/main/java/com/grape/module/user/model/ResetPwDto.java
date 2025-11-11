package com.grape.module.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResetPwDto {

    @NotBlank
    String userId;
    @NotBlank
    String newPassword;
    @NotBlank
    boolean isVerified;
}
