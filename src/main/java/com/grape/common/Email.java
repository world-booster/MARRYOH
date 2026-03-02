//package com.grape.common;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Component;
//
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.util.concurrent.TimeUnit;
//
///*
// * 이메일 인증 코드 전송 및 검증 공통 컴포넌트
// * **********[메서드 설명]*****************
// * 1. generateCode       : 6자리 인증 코드 생성 (숫자)
// * 2. sendVerificationCode : 인증 코드 이메일 전송 + Redis 저장
// * 3. verifyCode          : Redis에 저장된 인증 코드 검증 (1회용)
// * */
//@Component
//@RequiredArgsConstructor
//public class Email {
//
//    private static final Logger logger = LogManager.getLogger(Email.class);
//
//    private final JavaMailSender mailSender;
//    private final StringRedisTemplate redisTemplate;
//
//    @Value("${spring.mail.username}")
//    private String senderEmail;
//
//    @Value("${email.verify.pepper:}")
//    private String pepper;
//
//    private static final int CODE_LENGTH = 6;
//    private static final long CODE_TTL_MINUTES = 5;
//    private static final long SEND_COOLDOWN_SECOND = 10; // 스탬/폭탄 방지
//
//    private static final int MAX_VERIFY_ATTEMPTS = 5;
//    private static final long ATTEMPT_LOCK_MINUTES =10;
//
//    /* Redis key prefix */
//    private static final String KEY_PREFIX_VERIFY = "email:verify:";
//    private static final String KEY_PREFIX_COOLDOWN = "email:verify:cooldown:";
//    private static final String KEY_PREFIX_ATTEMPT = "email:verify:attempt:";
//
//    /*
//     * 인증 코드 생성
//     * SecureRandom 기반 6자리 숫자 코드
//     * */
//    public String generateCode() {
//        SecureRandom random = new SecureRandom();
//        StringBuilder code = new StringBuilder();
//        for (int i = 0; i < CODE_LENGTH; i++) {
//            code.append(random.nextInt(10));
//        }
//        return code.toString();
//    }
//
//    /*
//     * 인증 코드 이메일 전송
//     * - 이메일 정규화
//     * - 발송 쿨다운 체크
//     * - Redis 저장(코드 해싱) -> 이메일 발송
//     * - 이메일 발송 실패 시 Redis 롤백(삭제)
//     * */
//    public void sendVerificationCode(String toEmail) throws MessagingException {
//        if (toEmail == null || toEmail.isBlank()) {
//            throw new IllegalArgumentException("이메일 주소가 존재하지 않습니다.");
//        }
//
//        String normalizedEmail = normalizeEmail(toEmail);
//
//        String cooldownKey = KEY_PREFIX_COOLDOWN + emailKey(normalizedEmail);
//        if(redisTemplate.hasKey(cooldownKey)){
//            throw new IllegalStateException("인증 코드를 너무 자주 요청하셨습니다. 잠시후 다시 시도해주세요.");
//        }
//
//        String code = generateCode();
//        String verifyKey = KEY_PREFIX_VERIFY + emailKey(normalizedEmail);
//        String attempKey = KEY_PREFIX_ATTEMPT + emailKey(normalizedEmail);
//
//        String hashedCode = hashCode(normalizedEmail, code);
//
//        redisTemplate.opsForValue().set(
//                verifyKey,
//                hashedCode,
//                CODE_TTL_MINUTES,
//                TimeUnit.MINUTES
//        );
//
//        redisTemplate.opsForValue().set(
//                attempKey,
//                "0",
//                ATTEMPT_LOCK_MINUTES,
//                TimeUnit.MINUTES
//        );
//
//        redisTemplate.opsForValue().set(
//                cooldownKey,
//                "1",
//                SEND_COOLDOWN_SECOND,
//                TimeUnit.SECONDS
//        );
//
//        try{
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//            helper.setFrom(senderEmail);
//            helper.setTo(normalizedEmail);
//            helper.setSubject("이메일 인증 코드");
//            helper.setText(buildEmailBody(code), true);
//
//            mailSender.send(message);
//            logger.info("인증 코드 이메일 전송 완료: {}", normalizedEmail);
//        } catch (MessagingException e) {
//            redisTemplate.delete(verifyKey);
//            logger.error("인증 코드 이메일 전송 실패(롤백): {}" + normalizedEmail, e);
//            throw e;
//        } catch (RuntimeException e){
//            redisTemplate.delete(verifyKey);
//            logger.error("인증 코드 처리 중 오류(롤백): {}"+ normalizedEmail, e);
//            throw e;
//        }
//    }
//
//    /*
//     * 인증 코드 검증
//     * 성공 시 Redis에서 코드 삭제 (1회용)
//     * return true(성공) / false(실패)
//     * */
//    public boolean verifyCode(String email, String inputCode) {
//        if (email == null || inputCode == null || inputCode.isBlank()) {
//            return false;
//        }
//
//        String normalizedEmail = normalizeEmail(email);
//
//        String verifyKey = KEY_PREFIX_VERIFY + emailKey(normalizedEmail);
//        String attemptKey = KEY_PREFIX_ATTEMPT + emailKey(normalizedEmail);
//
//        String storedHashed = redisTemplate.opsForValue().get(verifyKey);
//        if(storedHashed == null){
//            logger.debug("인증 코드 만료 또는 미존재: {}", normalizedEmail);
//            return false;
//        }
//
//        int attempts = parseInt(redisTemplate.opsForValue().get(attemptKey), 0);
//        if(attempts >= MAX_VERIFY_ATTEMPTS) {
//            logger.debug("인증 코드 시도 횟수 초과로 잠금: {}", normalizedEmail);
//            return false;
//        }
//
//        String inputHashed = hashCode(normalizedEmail, inputCode);
//        if (storedHashed.equals(inputHashed)){
//            redisTemplate.delete(verifyKey);
//            redisTemplate.delete(attemptKey);
//            logger.debug("이메일 인증 성공: {}", normalizedEmail);
//            return true;
//        }
//
//        Long attemptsLong = redisTemplate.opsForValue().increment(attemptKey);
//        long newAttempts = (attemptsLong == null ? attempts + 1L : attemptsLong);
//
//        Long ttlSeconds = redisTemplate.getExpire(attemptKey, TimeUnit.SECONDS);
//        if(ttlSeconds == null || ttlSeconds == -1){
//            redisTemplate.expire(attemptKey, ATTEMPT_LOCK_MINUTES, TimeUnit.MINUTES);
//        }
//
//        logger.debug("인증 코드 불일치: {} (attempt={})", normalizedEmail, attempts);
//        return false;
//    }
//
//    /*
//     * 인증 이메일 HTML 본문 생성
//     * */
//    private String buildEmailBody(String code) {
//        return "<div style=\"font-family: 'Apple SD Gothic Neo', sans-serif; max-width: 400px; margin: 0 auto; padding: 20px;\">"
//                + "<h2 style=\"color: #333;\">이메일 인증 코드</h2>"
//                + "<p style=\"color: #555;\">아래 인증 코드를 입력해 주세요.</p>"
//                + "<div style=\"background: #f4f4f4; padding: 16px; text-align: center; "
//                + "font-size: 28px; font-weight: bold; letter-spacing: 8px; border-radius: 8px; margin: 20px 0;\">"
//                + code
//                + "</div>"
//                + "<p style=\"color: #999; font-size: 13px;\">" + CODE_TTL_MINUTES + "분 이내에 입력해 주세요.</p>"
//                + "</div>";
//    }
//
//    private String normalizeEmail(String email) {
//        return email.trim().toLowerCase();
//    }
//
//    private String emailKey(String normalizedEmail){
//        return sha256Hex(normalizedEmail);
//    }
//
//    private String hashCode(String normalizedEmail, String code){
//        return sha256Hex(normalizedEmail + ":" + code + ":" + (pepper == null ? "" : pepper));
//    }
//
//    private String sha256Hex(String input){
//        try{
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
//            StringBuilder sb = new StringBuilder(digest.length * 2);
//            for (byte b : digest) {
//                sb.append(String.format("%02x", b));
//            }
//            return sb.toString();
//        } catch (NoSuchAlgorithmException e) {
//            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
//        }
//    }
//
//    private int parseInt(String s, int defaultValue){
//        if(s == null) return defaultValue;
//        try{
//            return Integer.parseInt(s);
//        } catch (NumberFormatException e){
//            return defaultValue;
//        }
//    }
//}
