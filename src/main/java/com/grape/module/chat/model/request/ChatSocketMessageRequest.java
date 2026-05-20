package com.grape.module.chat.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSocketMessageRequest {

    private String roomKey;

    private String senderUserKey;

    private String messageType;

    private String messageContent;

    private String fileKey;
}