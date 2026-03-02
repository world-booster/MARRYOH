package com.grape.common.email.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class HashUtil {

    @Value("${email.verify.pepper:}")
    private String pepper;

    private String emailKey(String normalizedEmail){
        return sha256Hex(normalizedEmail);
    }

    public String hashCode(String normalizedEmail, String code){
        return sha256Hex(normalizedEmail + ":" + code + ":" + (pepper == null ? "" : pepper));
    }

    public String sha256Hex(String input){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
        }
    }

    private int parseInt(String s, int defaultValue){
        if(s == null) return defaultValue;
        try{
            return Integer.parseInt(s);
        } catch (NumberFormatException e){
            return defaultValue;
        }
    }
}
