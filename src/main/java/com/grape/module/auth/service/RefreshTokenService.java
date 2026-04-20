package com.grape.module.auth.service;

import com.grape.module.auth.exception.AuthErrorCode;
import com.grape.module.auth.exception.AuthException;
import com.grape.module.auth.repository.IAuthRepository;
import com.grape.utils.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final IAuthRepository authRepository;
    private final DateFormatter dateFormatter;

    public String createRefreshToken(String userKey) {
        if (userKey == null || userKey.isBlank()) {
            throw new AuthException(AuthErrorCode.TOKEN_MISSING);
        }

        deleteToken(userKey);

        String rawToken = refreshTokenGenerator();
        String hashedToken = hashRefreshToken(rawToken);
        String tokenKey = userKey + dateFormatter.getNowFormat();

        int result = authRepository.insertToken(hashedToken, tokenKey, userKey);
        if (result == 0) {
            throw new AuthException(AuthErrorCode.TOKEN_SAVE_FAILED);
        }

        return rawToken;
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

    public String selectRefreshToken(String userKey) {
        if (userKey == null || userKey.isBlank()) {
            throw new AuthException(AuthErrorCode.TOKEN_MISSING);
        }
        return authRepository.selectRefreshToken(userKey);
    }

    public int checkUserToken(String userKey) {
        if (userKey == null || userKey.isBlank()) {
            return 0;
        }
        return authRepository.existUserToken(userKey);
    }

    public int checkToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return 0;
        }

        String hashedToken = hashRefreshToken(refreshToken);
        return authRepository.existToken(hashedToken);
    }

    public int deleteToken(String userKey) {
        if (userKey == null || userKey.isBlank()) {
            throw new AuthException(AuthErrorCode.TOKEN_MISSING);
        }

        if (checkUserToken(userKey) == 0) {
            return 0;
        }

        return authRepository.deleteToken(userKey);
    }

    public int deleteTokenByToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthException(AuthErrorCode.TOKEN_MISSING);
        }

        String hashedToken = hashRefreshToken(refreshToken);

        if (authRepository.existToken(hashedToken) == 0) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        return authRepository.deleteToken(hashedToken);
    }

    public String selectUserKeyByToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthException(AuthErrorCode.TOKEN_MISSING);
        }

        String hashedToken = hashRefreshToken(refreshToken);

        if (authRepository.existToken(hashedToken) == 0) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        return authRepository.selectUserKeyByToken(hashedToken);
    }
}