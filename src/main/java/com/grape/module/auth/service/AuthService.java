package com.grape.module.auth.service;

import com.grape.common.email.EmailVerificationService;
import com.grape.common.email.model.VerifyType;
import com.grape.module.auth.model.TokenDto;
import com.grape.utils.DateFormatter;
import com.grape.module.auth.repository.IAuthRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService{

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    @Value("${auth.private-key-path}")
    private String privateKeyPath;
    @Value("${auth.public-key-path}")
    private String publicKeyPath;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final IAuthRepository authRepository;
    private final DateFormatter dateFormatter;
    private final EmailVerificationService email;

    @PostConstruct
    public void initKeys(){

        logger.info("==== AuthService PostConstruct 진입 ====");
        logger.info("privateKeyPath={}", privateKeyPath);
        logger.info("publicKeyPath={}", publicKeyPath);

        this.privateKey = loadPrivateKey(privateKeyPath);
        this.publicKey = loadPublicKey(publicKeyPath);

        logger.info("privateKey OK = {}", privateKey != null);
        logger.info("publicKey OK = {}", publicKey != null);
    }

    @Override
    public String createRefreshToken(String userKey) {
        deleteToken(userKey);

        if(checkUserToken(userKey) == 1) throw new RuntimeException("유저키가 존재하지 않습니다.");

        String token = refreshTokenGenerator(userKey);
        if(token == null) throw new RuntimeException("토큰 생성 실패");

        String hashedToken = hashRefreshToken(token);

        String tokenKey = userKey + dateFormatter.getNowFormat();
        int result = authRepository.insertToken(hashedToken,tokenKey,userKey);

        if(result == 0){
            throw new RuntimeException("토큰 저장 실패");
        }
        return token;
    }

    @Override
    public String selectRefreshToken(String userKey) {
        if(userKey == null){
            throw new IllegalArgumentException("유저키가 존재하지 않습니다.");
        }
        return authRepository.selectRefreshToken(userKey);
    }

    @Override
    public String createTempToken(String userKey) {
        long expiration = 5 * 20 * 1000; //5분 TTL

        return Jwts.builder()
                .setSubject(userKey)
                .claim("tokenType", "TEMP")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String jwtTokenGenerator(String userKey) {
        long expiration = 3600000; //1시간

        return Jwts.builder()
                .setSubject(userKey)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.privateKey, SignatureAlgorithm.RS256) //RSA 개인키
                .compact(); // 토큰 문자열 생성 (base64 URL-safe 인코딩), 합치기
    }

    @Override
    public String refreshTokenGenerator(String userKey){
        if(userKey == null){
            throw new IllegalArgumentException("유저가 존재하지 않습니다.");
        }
        return UUID.randomUUID().toString();
    }

    @Override
    public String hashRefreshToken(String refreshToken) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException nae){
            throw new IllegalArgumentException("SHA-256 알고리즘이 존재하지 않습니다: "+nae.getMessage());
        }
    }

    @Override
    public int checkUserToken(String userKey) {
        try{
            if(userKey == null) return 0;
            return authRepository.existUserToken(userKey);
        }catch (Exception e){
            throw e; //임시 exception 핸들링
        }
    }

    @Override
    public int checkToken(String refreshToken){
        try{
            if(refreshToken == null || refreshToken.isBlank()) return 0;

            String hashedToken = hashRefreshToken(refreshToken);
            return authRepository.existToken(hashedToken);
        }catch (Exception e){
            throw e; //임시 exception 핸들링
        }
    }

    @Override
    public Claims parseJwtToken(String token) {
        if(token == null || token.isBlank()){
            throw new IllegalArgumentException("JWT 토큰이 존재하지 않습니다.");
        }

        try{
            return Jwts.parserBuilder()
                    .setSigningKey(this.publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e){
            throw new RuntimeException("Access Token Expired");
        }catch (Exception e){
            throw new RuntimeException("JWT 검증 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifyAccessToken(String token) {
        Claims claims = parseJwtToken(token);
        Date now = new Date();

        return claims.getExpiration().after(now) &&
                !claims.containsKey("tokenType");
    }

    @Override
    public boolean verifyTempToken(String token) {
        Claims claims = parseJwtToken(token);
        Date now = new Date();

        return claims.getExpiration().after(now) &&
                "TEMP".equals(claims.get("tokenType"));
    }

    @Override
    public boolean verifyRefreshToken(String token, String userKey) {
        if(token == null || token.isBlank()){
            throw new IllegalArgumentException("리프레시 토큰 없음");
        }

        if(userKey == null || userKey.isBlank()){
            throw new IllegalArgumentException("유저키 없음");
        }

        String hashedToken = hashRefreshToken(token);

        String refreshTokenInDB = authRepository.selectRefreshToken(userKey);

        if(refreshTokenInDB == null){
            throw new RuntimeException("서버에 리프레시 토큰이 존재하지 않습니다.");
        }

        return hashedToken.equals(refreshTokenInDB);
    }

    @Override
    public int deleteToken(String userKey) {
        try{
            if(checkUserToken(userKey) == 0){
                return 0;
            }

            return authRepository.deleteToken(userKey);
        }catch (Exception e){
            throw new RuntimeException("토큰 삭제 실패", e);
        }
    }

    @Override
    public int deleteTokenByToken(String refreshToken) {
        try{
            return authRepository.deleteToken(refreshToken);
        }catch (Exception e){
            throw new RuntimeException("토큰 삭제 실패", e);
        }
    }

    @Override
    public String selectUserKeyByToken(String refreshToken){
        try{
            return authRepository.selectUserKeyByToken(refreshToken);
        }catch (Exception e){
            throw new RuntimeException("유저찾기(토큰) 실패", e);
        }
    }

    private static PrivateKey loadPrivateKey(String filepath) {
        try (FileInputStream fis = new FileInputStream(new File(filepath))){
            String key = new String(fis.readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        }catch(Exception e){
            throw new RuntimeException("개인키 로드 실패", e); //임시 exception 핸들링
        }
    }

    @Override
    public void sendEmailVerification(String email) throws MessagingException {
        this.email.send(email, VerifyType.USER);
    }

    @Override
    public boolean verifyEmailCode(String email, String code) {
        return this.email.verify(email, VerifyType.USER, code);
    }

    private static PublicKey loadPublicKey(String filepath) {
        try (FileInputStream fis = new FileInputStream(new File(filepath))){
            String key = new String(fis.readAllBytes())
                    .replace("-----BEGIN PUBLIC KEY-----","")
                    .replace("-----END PUBLIC KEY-----","")
                    .replaceAll("\\s","");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        }catch (Exception e){
            throw new RuntimeException("공개키 로드 실패", e);
        }
    }
}
