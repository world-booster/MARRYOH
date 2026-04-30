package com.grape.module.user.exception;

import com.grape.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    /*  400 BAD REQUEST */
    INVALID_INPUT_VALUE("USER-400-001", HttpStatus.BAD_REQUEST, "Invalid input value."),
    INVALID_EMAIL_FORMAT("USER-400-002", HttpStatus.BAD_REQUEST, "Invalid email format."),
    INVALID_PASSWORD_FORMAT("USER-400-003", HttpStatus.BAD_REQUEST, "Invalid password format."),
    INVALID_USER_ID_FORMAT("USER-400-004", HttpStatus.BAD_REQUEST, "Invalid user ID format."),
    INVALID_NICKNAME_FORMAT( "USER-400-005", HttpStatus.BAD_REQUEST, "Invalid user nickname format."),
    PASSWORD_CONFIRM_MISMATCH("USER-400-006", HttpStatus.BAD_REQUEST, "Password confirmation does not match."),

    /* 404 NOT FOUND */
    USER_NOT_FOUND("USER-404-001", HttpStatus.NOT_FOUND, "User not found."),
    USER_ID_NOT_FOUND("USER-404-002", HttpStatus.NOT_FOUND, "User ID not found."),
    EMAIL_NOT_FOUND("USER-404-003", HttpStatus.NOT_FOUND, "Email not found."),

    /* 409 CONFLICT */
    DUPLICATE_USER_ID("USER-409-001", HttpStatus.CONFLICT, "User ID is already in use."),
    DUPLICATE_EMAIL("USER-409-002", HttpStatus.CONFLICT, "Email is already in use."),
    DUPLICATE_USER("USER-409-003", HttpStatus.CONFLICT, "User already exists."),
    DUPLICATE_NICKNAME("USER-409-004", HttpStatus.CONFLICT, "Nickname is already in use."),

    /* 인증/검증 */
    PASSWORD_NOT_MATCH("USER-401-001", HttpStatus.UNAUTHORIZED, "Password does not match."),
    EMAIL_VERIFICATION_FAILED("USER-401-002", HttpStatus.UNAUTHORIZED, "Email verification failed."),
    VERIFICATION_CODE_INVALID("USER-401-003", HttpStatus.UNAUTHORIZED, "Invalid verification code."),
    VERIFICATION_CODE_EXPIRED("USER-401-004", HttpStatus.UNAUTHORIZED, "Verification code has expired."),

    /* 403 FORBIDDEN */
    ACCESS_DENIED_USER("USER-403-001", HttpStatus.FORBIDDEN, "Access denied."),

    /* password / account */
    PASSWORD_SAME_AS_OLD("USER-400-006", HttpStatus.BAD_REQUEST, "New password must be different from the old password."),
    PASSWORD_CHANGE_FAILED("USER-500-001", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to change password."),

    /* server error */
    USER_SAVE_FAILED("USER-500-002", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save user."),
    USER_UPDATE_FAILED("USER-500-003", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update user."),
    USER_DELETE_FAILED("USER-500-004", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete user.");

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
