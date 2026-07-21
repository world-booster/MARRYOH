package com.grape.module.chat.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSocketMessageResponse {

    private String messageKey;

    private String roomKey;

    private String senderUserKey;

    private String messageType;

    private String messageContent;

    private String fileKey;

    private LocalDateTime createdAt;

    private String senderNickname;

    private Integer readCount;
}