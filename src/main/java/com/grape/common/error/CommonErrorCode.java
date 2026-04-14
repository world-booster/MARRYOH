package com.grape.common.error;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode  implements ErrorCode{
    VALIDATION_ERROR("COMMON-400-VALIDATION", HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    BAD_REQUEST("COMMON-400-BAD_REQUEST", HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다."),
    UNAUTHORIZED("COMMON-401-UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN("COMMON-404-NOT_FOUND", HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    INTERNAL_ERROR("COMMON-500-UNKNOWN", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    CommonErrorCode(String code, HttpStatus status, String message){
        this.code = code;
        this.status = status;
        this.message = message;
    }

    @Override public String code() { return code; }
    @Override public HttpStatus httpStatus() { return status; }
    @Override public String defaultMessage() { return message; }
}
