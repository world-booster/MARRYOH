package com.grape.common.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
        String code();              // ex) USER-404-001
        HttpStatus httpStatus();    // ex) 404
        String defaultMessage();    // 기본 메세지
}
