package com.grape.module.auth.service;

import com.grape.common.email.EmailVerificationService;
import com.grape.common.email.model.VerifyType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private final EmailVerificationService email;

    public String jwtTokenGenerator(String userKey) {
        return jwtService.generateAccessToken(userKey);
    }

    public String createRefreshToken(String userKey) {
        return refreshTokenService.createRefreshToken(userKey);
    }

    public String selectRefreshToken(String userKey) {
        return refreshTokenService.selectRefreshToken(userKey);
    }

    public int checkUserToken(String userKey) {
        return refreshTokenService.checkUserToken(userKey);
    }

    public int checkToken(String refreshToken) {
        return refreshTokenService.checkToken(refreshToken);
    }

    public int deleteToken(String userKey) {
        return refreshTokenService.deleteToken(userKey);
    }

    public int deleteTokenByToken(String refreshToken) {
        return refreshTokenService.deleteTokenByToken(refreshToken);
    }

    public String selectUserKeyByToken(String refreshToken) {
        return refreshTokenService.selectUserKeyByToken(refreshToken);
    }

    public String hashRefreshToken(String refreshToken) {
        return refreshTokenService.hashRefreshToken(refreshToken);
    }

    public void sendEmailVerification(String email) throws MessagingException {
        this.email.send(email, VerifyType.USER);
    }

    public boolean verifyEmailCode(String email, String code) {
        return this.email.verify(email, VerifyType.USER, code);
    }
}