package com.grape.common.error;

import java.time.Instant;

public record ErrorResponse (
        String code,
        String message,
        String traceId,
        Instant timestamp,
        String path
){
    public static ErrorResponse of(String code, String message, String traceId, String path){
        return new ErrorResponse(code, message, traceId, Instant.now(), path);
    }

}
