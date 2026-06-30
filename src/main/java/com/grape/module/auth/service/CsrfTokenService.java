package com.grape.module.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CsrfTokenService {

    @Value("${app.csrf.secret}")
    String csrfSecret;

    public String createCsrfToken() {
        try{
            String nonce = UUID.randomUUID().toString();
            String signature = createSignature(nonce);

            return nonce + "." + signature;
        } catch (Exception e){
            throw new RuntimeException("CSRF TOKEN GENERATE FAILED", e);
        }
    }

    public boolean validateCsrfToken(String csrfToken){
        try{
            if(csrfToken == null){
                return false;
            }

            String[] parts = csrfToken.split("\\.");

            if(parts.length != 2){
                return false;
            }

            String nonce = parts[0];
            String signature = parts[1];
            String expected = createSignature(nonce);

            return expected.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private String createSignature(String nonce) throws Exception{
        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec keySpec = new SecretKeySpec(csrfSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        mac.init(keySpec);

        byte[] result = mac.doFinal(nonce.getBytes(StandardCharsets.UTF_8));

        return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
    }
}
