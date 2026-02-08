package com.grape.module.auth.controller;

import com.grape.module.auth.service.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class EmailController {

    private final IEmailService emailService;

    @PostMapping("/email-request")
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        try{
            emailService.sendEmail(email);
            return ResponseEntity.ok("인증 코드가 전송되었습니다.");
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("이메일 전송 실패:" + e.getMessage());
        }
    }

    @PostMapping("/email-verify")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean result = emailService.verifyEmail(email, code);
        return result ? ResponseEntity.ok("이메일 인증 완료") : ResponseEntity.status(400).body("인증 실패 또는 만료된 코드입니다.");
    }
}
