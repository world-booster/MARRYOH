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
public class ChatRoomUserDto {

    private String roomUserKey;
    private String roomKey;
    private String userKey;
    private String lastReadMessageKey;
    private LocalDateTime joinedAt;
    private LocalDateTime exitedAt;
    private String notificationYn;
    private String favoriteYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}