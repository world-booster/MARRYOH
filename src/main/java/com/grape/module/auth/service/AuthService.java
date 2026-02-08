package com.grape.module.auth.service;


import com.grape.common.DateFormat;
import com.grape.module.auth.repository.IAuthRepository;
import com.grape.module.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService{

    private static final Logger logger = LogManager.getLogger(AuthService.class);

//    private static final PrivateKey PRIVATE_KEY = loadPrivateKey("D:\\Programming\\grape_openssl\\private_key.pem");
//    private static final PublicKey PUBLIC_KEY = loadPublicKey("D:\\Programming\\grape_openssl\\public_key.pem");
    @Value("${auth.private-key-path}")
    private String privateKeyPath;
    @Value("${auth.public-key-path}")
    private String publicKeyPath;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final IAuthRepository authRepository;
    private final DateFormat dateFormat;

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
    public int createRefreshToken(String userKey) {
        deleteToken(userKey);

        if(checkUserToken(userKey) == 1) return 0;

        String token = jwtTokenGenerator(userKey);
        if(token == null) return 0;

        String tokenKey = userKey + dateFormat.getNowFormat();
        System.out.println("토큰키길이:"+tokenKey.length());
        return authRepository.insertToken(token,tokenKey,userKey);
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
    public int checkUserToken(String userKey) {
        try{
            if(userKey == null) return 0;
            return authRepository.existUserToken(userKey);
        }catch (Exception e){
            throw e; //임시 exception 핸들링
        }
    }

    @Override
    public Claims parseJwtToken(String token) {
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
    public boolean verifyRefreshToken(String token) {
        Claims claims = parseJwtToken(token);
        Date now = new Date();

        String userKey = claims.getSubject();
        String storedToken = selectRefreshToken(userKey);

        return claims.getExpiration().after(now) &&
                storedToken != null &&
                storedToken.equals(token);
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
