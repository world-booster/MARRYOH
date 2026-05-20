package com.grape.module.chat.utils;

import java.util.UUID;

public class ChatKeyGenerator {

    private ChatKeyGenerator() {
    }

    public static String generateRoomKey() {
        return UUID.randomUUID().toString();
    }

    public static String generateRoomUserKey() {
        return UUID.randomUUID().toString();
    }

    public static String generateMessageKey() {
        return UUID.randomUUID().toString();
    }

    public static String generateMessageReadKey() {
        return UUID.randomUUID().toString();
    }

    public static String generateEventLogKey() {
        return UUID.randomUUID().toString();
    }

    public static String generateFileKey() {
        return UUID.randomUUID().toString();
    }

    public static String generateInviteLogKey() {
        return UUID.randomUUID().toString();
    }
}