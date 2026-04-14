package com.grape.module.auth.exception;

import com.grape.common.error.BaseException;
import com.grape.common.error.ErrorCode;

public class AuthException extends BaseException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message){
        super(errorCode, message);
    }
}
