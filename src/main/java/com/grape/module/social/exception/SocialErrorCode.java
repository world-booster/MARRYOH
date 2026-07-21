package com.grape.module.social.exception;

import com.grape.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum SocialErrorCode implements ErrorCode {
    /* OAuth2 (External API)*/
    TOKEN_REQUEST_FAILED("SOCIAL-401-001", HttpStatus.UNAUTHORIZED, "social token request failed"),
    TOKEN_INVALID("SOCIAL-401-002", HttpStatus.UNAUTHORIZED, "social token is not valid"),
    USER_INFO_REQUEST_FAILED("SOCIAL-401-003", HttpStatus.UNAUTHORIZED, "social user info request failed"),
    USER_INFO_INVALID("SOCIAL-401-004", HttpStatus.UNAUTHORIZED, "social user info is not invalid"),
    NETWORK_ERROR("SOCIAL-503-001", HttpStatus.SERVICE_UNAVAILABLE, "social server network error"),

    /* Profile (Data Verification)*/
    EMAIL_REQUIRED("SOCIAL-400-001", HttpStatus.BAD_REQUEST, "need email"),
    PROVIDER_ID_REQUIRED("SOCIAL-400-002", HttpStatus.BAD_REQUEST, "need provider id"),
    UNSUPPORTED_PROVIDER("SOCIAL-400-003", HttpStatus.BAD_REQUEST, "unsupported provider"),

    /* Signup (Business)*/
    DUPLICATE_USER("SOCIAL-409-001", HttpStatus.CONFLICT, "this social profile is already registered"),
    SIGNUP_FAILED("SOCIAL-500-001", HttpStatus.INTERNAL_SERVER_ERROR, "social signup failed");

    private final String code;
    private final HttpStatus status;
    private final String message;


    @Override
    public String code() {
        return code;
    }

    @Override
    public HttpStatus httpStatus() {
        return status;
    }

    @Override
    public String defaultMessage() {
        return message;
    }
}
