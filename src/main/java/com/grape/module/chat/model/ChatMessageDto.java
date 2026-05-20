package com.grape.module.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private String messageKey;

    private String roomKey;

    private String messageType;

    private String senderUserKey;

    private String messageContent;

    private String fileKey;

    private LocalDateTime createdAt;

    private String deletedYn;
}