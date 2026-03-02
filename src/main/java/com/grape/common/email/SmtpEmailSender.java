package com.grape.common.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendVerifivationCode(String email, String code) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(email);
            helper.setSubject("이메일 인증 코드");
            helper.setText(buildEmailBody(code), true);
            mailSender.send(message);
        } catch (Exception e){
            throw new RuntimeException("메일 발송 실패", e);
        }
    }

    private String buildEmailBody(String code){
        return "<div style=\"font-family: 'Apple SD Gothic Neo', sans-serif; max-width: 400px; margin: 0 auto; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">이메일 인증 코드</h2>"
                + "<p style=\"color: #555;\">아래 인증 코드를 입력해 주세요.</p>"
                + "<div style=\"background: #f4f4f4; padding: 16px; text-align: center; "
                + "font-size: 28px; font-weight: bold; letter-spacing: 8px; border-radius: 8px; margin: 20px 0;\">"
                + code
                + "</div>"
                + "<p style=\"color: #999; font-size: 13px;\">요청하지 않으셨다면 이 메일을 무시하세요.</p>"
                + "</div>";
    }
}
