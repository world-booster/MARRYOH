package com.grape.common.email;

import com.grape.common.email.model.VerifyType;
import com.grape.common.email.mybatis.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class EmailMybatisVerificationStore implements EmailVerificationStore {

    private final EmailVerificationRepository repository;

    @Override
    public void upsertSesstion(String emailHash, VerifyType type, String codeHash, Instant expiresAt, Instant now) {
        repository.upsertSession(emailHash, type.name(), codeHash, expiresAt, now);
    }

    @Override
    public PolicyRow findPolicyRow(String emailHash, VerifyType type, Instant now) {
        var row = repository.selectPolicyRow(emailHash, type.name());
        if(row == null) return null;
        return new PolicyRow(row.lastSentAt(), row.lockedUntil());
    }

    @Override
    public boolean markUsedIfMatch(String emailHash, VerifyType type, String codeHash, Instant now) {
        return repository.markUsedIfMatch(emailHash, type.name(), codeHash, now) == 1;
    }

    @Override
    public void increaseAttempts(String emailHash, VerifyType type, int maxAttempts, Instant lockedUntil, Instant now) {
        repository.increaseAttempts(emailHash, type.name(), maxAttempts, lockedUntil, now);
    }
}
