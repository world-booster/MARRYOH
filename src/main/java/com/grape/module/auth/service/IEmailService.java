package com.grape.module.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public interface IEmailService {

    /*
    * 1. 랜덤 인증 코드 생성 : createCode
    * 2. 인증 코드 메일 생성 : createEmailForm
    * 3. 인증 메일 전송 : sendEmail
    * 4. 인증 코드 검증 : verifyEmail
    * */

    String createCode();
    MimeMessage createEmailForm(String toEmail, String code) throws MessagingException;
    void sendEmail(String toEmail) throws MessagingException;
    boolean verifyEmail(String email, String inputCode);
}
