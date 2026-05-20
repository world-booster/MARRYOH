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
public class ChatRoomResponse {

    private String roomKey;

    private String roomName;

    private String roomType;

    private String lastMessageContent;

    private LocalDateTime lastMessageAt;

    private LocalDateTime createdAt;

    private Integer unreadCount;

    private boolean joined;
}