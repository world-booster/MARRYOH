package com.grape.module.social.exception;

import com.grape.common.error.BaseException;
import com.grape.common.error.ErrorCode;

public class SocialException extends BaseException {

  public SocialException(ErrorCode errorCode) {
    super(errorCode);
  }

  public SocialException(ErrorCode errorCode, String customMessage) {
    super(errorCode, customMessage);
  }
}
