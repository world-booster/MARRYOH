package com.grape.module.auth.service;

import com.grape.common.email.EmailVerificationService;
import com.grape.common.email.model.VerifyType;
import com.grape.module.auth.exception.AuthErrorCode;
import com.grape.module.auth.exception.AuthException;
import com.grape.utils.DateFormatter;
import com.grape.module.auth.repository.IAuthRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
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
public class TokenService {

    private static final Logger logger = LogManager.getLogger(TokenService.class);
    private final IAuthRepository authRepository;
    private final DateFormatter dateFormatter;
    private final EmailVerificationService email;
    @Value("${auth.private-key-path}")
    private String privateKeyPath;
    @Value("${auth.public-key-path}")
    private String publicKeyPath;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private static PrivateKey loadPrivateKey(String filepath) {
        try (FileInputStream fis = new FileInputStream(new File(filepath))) {
            String key = new String(fis.readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.KEY_LOAD_FAILED);
        }
    }

    private static PublicKey loadPublicKey(String filepath) {
        try (FileInputStream fis = new FileInputStream(new File(filepath))) {
            String key = new String(fis.readAllBytes())
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.KEY_LOAD_FAILED);
        }
    }

    @PostConstruct
    public void initKeys() {

        logger.info("==== AuthService PostConstruct 진입 ====");
        logger.info("privateKeyPath={}", privateKeyPath);
        logger.info("publicKeyPath={}", publicKeyPath);

        this.privateKey = loadPrivateKey(privateKeyPath);
        this.publicKey = loadPublicKey(publicKeyPath);

        logger.info("privateKey OK = {}", privateKey != null);
        logger.info("publicKey OK = {}", publicKey != null);
    }

    public String createRefreshToken(String userKey) {
        if (userKey == null || userKey.isBlank()) {
            throw new AuthException(AuthErrorCode.TOKEN_MISSING);
        }

        deleteToken(userKey);

        String token = refreshTokenGenerator();
        String hashedToken = hashRefreshToken(token);

        String tokenKey = userKey + dateFormatter.getNowFormat();
        int result = authRepository.insertToken(hashedToken, tokenKey, userKey);

        if (result == 0) {
            throw new AuthException(AuthErrorCode.TOKEN_SAVE_FAILED);
        }
        return token;
    }

    public String selectRefreshToken(String userKey) {
        if (userKey == null) {
            throw new AuthException(AuthErrorCode.TOKEN_MISSING);
        }
        return authRepository.selectRefreshToken(userKey);
    }

    public String jwtTokenGenerator(String userKey) {
        long expiration = 3600000; //1시간

        return Jwts.builder()
                .setSubject(userKey)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.privateKey, SignatureAlgorithm.RS256) //RSA 개인키
                .compact(); // 토큰 문자열 생성 (base64 URL-safe 인코딩), 합치기
    }

    public String refreshTokenGenerator() {
        return UUID.randomUUID().toString();
    }

    public String hashRefreshToken(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID_FORMAT);
        }
    }

    public int checkUserToken(String userKey) {
        if (userKey == null) return 0;
        return authRepository.existUserToken(userKey);
    }

    public int checkToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return 0;

        String hashedToken = hashRefreshToken(refreshToken);
        return authRepository.existToken(hashedToken);
    }

    public Claims parseJwtToken(String token) {
        if (token == null || token.isBlank()) {
            throw new AuthException(AuthErrorCode.TOKEN_MISSING);
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(this.publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new AuthException(AuthErrorCode.TOKEN_MALFORMED);
        } catch (UnsupportedJwtException e) {
            throw new AuthException(AuthErrorCode.TOKEN_UNSUPPORTED);
        } catch (SignatureException e) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID_SIGNATURE);
        } catch (IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID_FORMAT);
        }
    }

    public boolean verifyAccessToken(String token) {
        Claims claims = parseJwtToken(token);
        Date now = new Date();

        return claims.getExpiration().after(now) &&
                !claims.containsKey("tokenType");
    }

    public int deleteToken(String userKey) {

        if (checkUserToken(userKey) == 0) {
            return 0;
        }

        return authRepository.deleteToken(userKey);

    }

    public int deleteTokenByToken(String refreshToken) {
        if(refreshToken == null || refreshToken.isBlank()){
            throw new AuthException(AuthErrorCode.TOKEN_MISSING);
        }

        String hashedToken = hashRefreshToken(refreshToken);

        if (authRepository.existToken(hashedToken) == 0) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        return authRepository.deleteToken(refreshToken);
    }

    public String selectUserKeyByToken(String refreshToken) {
        if(refreshToken == null || refreshToken.isBlank()){
            throw new AuthException(AuthErrorCode.TOKEN_MISSING);
        }

        String hashedToken = hashRefreshToken(refreshToken);

        if (authRepository.existToken(hashedToken) == 0) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
            return authRepository.selectUserKeyByToken(refreshToken);

    }

    public void sendEmailVerification(String email) throws MessagingException {
        this.email.send(email, VerifyType.USER);
    }

    public boolean verifyEmailCode(String email, String code) {
        return this.email.verify(email, VerifyType.USER, code);
    }
}
