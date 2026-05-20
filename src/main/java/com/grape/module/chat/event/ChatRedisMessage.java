package com.grape.module.chat.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRedisMessage {

    private String roomKey;

    private String senderUserKey;

    private String messageType;

    private String messageContent;

    private String fileKey;
}