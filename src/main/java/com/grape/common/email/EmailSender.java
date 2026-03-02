package com.grape.common.email;

public interface EmailSender {
    void sendVerifivationCode(String email, String code);
}
