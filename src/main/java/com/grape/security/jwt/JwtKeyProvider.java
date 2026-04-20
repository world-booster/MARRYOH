package com.grape.security.jwt;

import com.grape.module.auth.exception.AuthErrorCode;
import com.grape.module.auth.exception.AuthException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Getter
@Component
@RequiredArgsConstructor
public class JwtKeyProvider {

    private final JwtProperties jwtProperties;
    private final ResourceLoader resourceLoader;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        this.privateKey = loadPrivateKey(jwtProperties.getPrivateKeyPath());
        this.publicKey = loadPublicKey(jwtProperties.getPublicKeyPath());
    }

    private PrivateKey loadPrivateKey(String location) {
        try {
            System.out.println("LOCATION = " + location);

            Resource resource = resourceLoader.getResource(location);
            System.out.println("EXISTS = " + resource.exists());
            System.out.println("FILENAME = " + resource.getFilename());

            try (InputStream is = resource.getInputStream()) {
                String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("\uFEFF", "")
                        .replaceAll("-----BEGIN (.*)-----", "")
                        .replaceAll("-----END (.*)-----", "")
                        .replaceAll("\\s", "");

                System.out.println("KEY LENGTH = " + key.length());
                System.out.println("KEY PREVIEW = " + key.substring(0, Math.min(50, key.length())));

                byte[] keyBytes = Base64.getDecoder().decode(key);
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePrivate(spec);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthException(AuthErrorCode.KEY_LOAD_FAILED);
        }
    }

//    private PrivateKey loadPrivateKey(String location) {
//        try (InputStream is = open(location)) {
//
//            String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
//                    .replace("\uFEFF", "")
//                    .replaceAll("-----BEGIN (.*)-----", "")
//                    .replaceAll("-----END (.*)-----", "")
//                    .replaceAll("\\s", "");
//
//            System.out.println("RAW START >>>");
//            System.out.println(key.substring(0, 50));
//            System.out.println("RAW END <<<");
//
//            System.out.println("KEY LENGTH = " + key.length());
//
//            byte[] keyBytes = Base64.getDecoder().decode(key);
//            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            return keyFactory.generatePrivate(spec);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new AuthException(AuthErrorCode.KEY_LOAD_FAILED);
//        }
//    }

    private PublicKey loadPublicKey(String location) {
        try (InputStream is = open(location)) {
            String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("\uFEFF", "")
                    .replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthException(AuthErrorCode.KEY_LOAD_FAILED);
        }
    }

    private InputStream open(String location) throws Exception {
        Resource resource = resourceLoader.getResource(location);
        return resource.getInputStream();
    }
}