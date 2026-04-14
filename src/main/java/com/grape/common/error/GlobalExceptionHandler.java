package com.grape.common.error;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    /* Domain Exception */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException e, HttpServletRequest request) {
        var errorCode = e.getErrorCode();

        /*  need stacktrace in operation level, use warn/error
        *  it is enough to use warn, if exception of business occurs */
        logger.warn("Business exception: code={}, messge={}, path={}",
                errorCode.code(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(errorCode.httpStatus())
                .body(ErrorResponse.of(errorCode.code(), e.getMessage(), TraceId.get(), request.getRequestURI()));
    }

    /* @Valid DTO verifying failed */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request){
        String message = firstFieldErrorMessage(e).orElse(CommonErrorCode.VALIDATION_ERROR.defaultMessage());
        var errorCode = CommonErrorCode.VALIDATION_ERROR;

        return ResponseEntity.status(errorCode.httpStatus())
                .body(ErrorResponse.of(errorCode.code(), message, TraceId.get(), request.getRequestURI()));
    }

    /* Json parsing failed, parameter type error ... */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e, HttpServletRequest request){
        var errorCode = CommonErrorCode.BAD_REQUEST;

        logger.warn("Bad reqeust: {} path={}", e.getClass().getSimpleName(), request.getRequestURI());

        return ResponseEntity.status(errorCode.httpStatus())
                .body(ErrorResponse.of(errorCode.code(), errorCode.defaultMessage(), TraceId.get(), request.getRequestURI()));
    }

    /* Unknown Error */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e, HttpServletRequest request){
        var errorCode = CommonErrorCode.INTERNAL_ERROR;

        logger.error("Unhandled exception path={}", request.getRequestURI(), e);

        return ResponseEntity.status(errorCode.httpStatus())
                .body(ErrorResponse.of(errorCode.code(), errorCode.defaultMessage(), TraceId.get(), request.getRequestURI()));
    }

    private Optional<String> firstFieldErrorMessage(MethodArgumentNotValidException e){
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        if (fieldError == null) return Optional.empty();
        return Optional.of(fieldError.getField() + ": " + fieldError.getDefaultMessage());
    }
}
