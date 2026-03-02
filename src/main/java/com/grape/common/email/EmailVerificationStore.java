package com.grape.common.email;

import com.grape.common.email.model.VerifyType;

import java.time.Duration;
import java.time.Instant;

public interface EmailVerificationStore {

    void upsertSesstion(String emailHash, VerifyType type, String codeHash, Instant expiresAt, Instant now);

    /*
    * cooldown/lock checking for at least information
    * */
    PolicyRow findPolicyRow(String emailHash, VerifyType type, Instant now);

    /*
    * if condition is checked, change state to "used" (atomic)
    * */
    boolean markUsedIfMatch(String emailHash, VerifyType type, String codeHash, Instant now);

    /*
    * if failed, increase attempts && change state to "lock"
    * */
    void increaseAttempts(String emailHash, VerifyType type, int maxAttempts, Instant lockedUntil, Instant now);

    record PolicyRow(
            Instant lastSentAt,
            Instant lockedUntil
    ){
        public boolean isInCooldown(Instant now, Duration cooldown){
            return lastSentAt != null && now.isBefore(lastSentAt.plus(cooldown));
        }
        public boolean isLocked(Instant now){
            return lockedUntil != null && now.isBefore(lockedUntil);
        }
    }

}
