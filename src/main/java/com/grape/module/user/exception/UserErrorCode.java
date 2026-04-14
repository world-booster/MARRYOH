package com.grape.module.user.exception;

import com.grape.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER-404-001", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EMAIL_DUPLICATED("USER-409-001", HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    PASSWORD_MISMATCH("USER-401-001", HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.");

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
