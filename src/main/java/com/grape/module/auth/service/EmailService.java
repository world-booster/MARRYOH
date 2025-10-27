package com.grape.module.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String SENDER_EMAIL = "rldud96@gmail.com";

    @Override
    public String createCode() {
        return UUID.randomUUID().toString().substring(0,6).toUpperCase();
    }

    @Override
    public MimeMessage createEmailForm(String toEmail, String code) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(SENDER_EMAIL);
        message.setRecipients(MimeMessage.RecipientType.TO, toEmail);
        message.setSubject("이메일 인증 코드");
        String body = "<h2>이메일 인증 코드: " + code + "</h2><p>5분 이내에 입력해 주세요.</p>";
        message.setContent(body, "text/html; charset=utf-8");
        return message;
    }

    @Override
    public void sendEmail(String toEmail) throws MessagingException {
        String code = createCode();
        MimeMessage message = createEmailForm(toEmail, code);
        mailSender.send(message);
        redisTemplate.opsForValue().set("email:" + toEmail, code, 5, TimeUnit.MINUTES);
    }

    @Override
    public boolean verifyEmail(String email, String inputCode) {
        String storedCode = (String) redisTemplate.opsForValue().get("email:"+email);
        return storedCode != null && storedCode.equals(inputCode);
    }
}
