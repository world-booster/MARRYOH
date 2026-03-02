package com.grape.common.email;

import com.grape.common.email.model.VerifyType;
import com.grape.common.email.util.HashUtil;
import com.grape.config.security.SecurityEmailProperties;
import com.grape.module.auth.controller.AuthController;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final EmailVerificationStore store;
    private final EmailSender emailSender;
    private final SecurityEmailProperties props;
    private final HashUtil util;

    private static final int CODE_LENGTH = 6;

    private static final Logger logger = LogManager.getLogger(EmailVerificationService.class);

    @Transactional
    public void send(String email, VerifyType type){
        try{
            String normalized = normalizeEmail(email);
            String emailHash = util.sha256Hex(normalized);

            Instant now = Instant.now();

            var policyRow = store.findPolicyRow(emailHash, type, now);
            if(policyRow != null){
                if(policyRow.isInCooldown(now, props.cooldown())){
                    throw new IllegalStateException("인증 코드를 너무 자주 요청하셨습니다. 잠시후 시도해주세요.");
                }
                if(policyRow.isLocked(now)){
                    throw new IllegalStateException("인증 시도 횟수가 초과되어 10분간 잠금 상태로 전환됩니다.");
                }
            }

            String code = generateCode();
            String codeHash = util.hashCode(normalized, code);

            Instant expiresAt = now.plus(props.ttl());

            store.upsertSesstion(emailHash, type, codeHash, expiresAt, now);

            emailSender.sendVerifivationCode(normalized, code);

        }catch (Exception e){
            logger.debug("메일 발송 실패 원인", e);
            throw new RuntimeException("메일 발송 실패", e);
        }

    }

    @Transactional
    public boolean verify(String email, VerifyType type, String inputCode){
        String normalized = normalizeEmail(email);
        String emailHash = util.sha256Hex(email);
        Instant now = Instant.now();

        String codeHash = util.hashCode(normalized, inputCode);

        boolean ok = store.markUsedIfMatch(emailHash, type, codeHash,now);
        if(ok) return true;

        store.increaseAttempts(emailHash, type, props.maxAttempts(), now.plus(props.lockTime()), now);
        return false;
    }


    private String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }


}
