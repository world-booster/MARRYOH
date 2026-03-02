package com.grape.common.email.mybatis;

import com.grape.common.email.EmailVerificationStore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.Instant;

@Mapper
public interface EmailVerificationRepository {

    /*
    * 발송: email_hash, type 기준 Upsert
    * */
    int upsertSession(@Param("emailHash") String emailHash,
                      @Param("type") String type,
                      @Param("codeHash") String codeHash,
                      @Param("expiresAt")Instant expiresAt,
                      @Param("now") Instant now);

    /*
    * 발송 제한(쿨다운/잠금)을 위해 필요한 필드 조회
    * */
    EmailVerificationStore.PolicyRow selectPolicyRow(@Param("emailHash") String emailHash,
                                                     @Param("type") String type);

    /*
    * 검증 성공: 조건을 만족하면 used_at 찍기 (성공:1 실패:0)
    * */
    int markUsedIfMatch(@Param("emailHash") String emailHash,
                        @Param("type") String type,
                        @Param("codeHash") String codeHash,
                        @Param("now") Instant now);

    /*
    * 검증 실패: attempts 증가 + 필요 시 lock 설정
    * */
    int increaseAttempts(@Param("emailHash") String emailHash,
                         @Param("type") String type,
                         @Param("maxAttempts") int maxAttempts,
                         @Param("lockedUntil") Instant lockedUntil,
                         @Param("now") Instant now);
}
