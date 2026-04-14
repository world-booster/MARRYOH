package com.grape.module.auth.exception;

import com.grape.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    TOKEN_INVALID_FORMAT("AUTH-400-001", HttpStatus.BAD_REQUEST, "인증 토큰 형식이 올바르지 않습니다."),
    TOKEN_MISSING("AUTH-401-001", HttpStatus.UNAUTHORIZED, "인증 토큰이 존재하지 않습니다."),
    TOKEN_INVALID_SIGNATURE("AUTH-401-002", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 서명입니다."),
    TOKEN_EXPIRED("AUTH-401-003", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_MALFORMED("AUTH-401-004", HttpStatus.UNAUTHORIZED, "손상되었거나 잘못된 토큰입니다."),
    TOKEN_UNSUPPORTED("AUTH-401-005", HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND("AUTH-401-006", HttpStatus.UNAUTHORIZED, "저장된 리프레시 토큰이 없습니다."),
    REFRESH_TOKEN_MISMATCH("AUTH-401-007", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다."),
    TOKEN_REVOKED("AUTH-401-008", HttpStatus.UNAUTHORIZED, "폐기된 토큰입니다."),
    ACCESS_DENIED("AUTH-403-001", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    KEY_LOAD_FAILED("AUTH-500-001", HttpStatus.INTERNAL_SERVER_ERROR, "키 파일 로드에 실패했습니다."),
    TOKEN_SAVE_FAILED("AUTH-500-002", HttpStatus.INTERNAL_SERVER_ERROR, "토큰 저장에 실패했습니다.");


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
