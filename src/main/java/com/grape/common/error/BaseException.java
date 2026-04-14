package com.grape.common.error;

public abstract class BaseException extends RuntimeException{

    private final ErrorCode errorCode;

    protected BaseException(ErrorCode errorCode){
        super(errorCode.defaultMessage());
        this.errorCode = errorCode;
    }

    protected  BaseException(ErrorCode errorCode, String customMessage){
        super(customMessage);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
