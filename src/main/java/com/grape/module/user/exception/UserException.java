package com.grape.module.user.exception;

import com.grape.common.error.BaseException;
import com.grape.common.error.ErrorCode;

public class UserException extends BaseException {
    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(UserErrorCode errorCode, String message){
        super(errorCode, message);
    }
}
