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
public class ChatPresenceDto {

    private String userKey;

    private String userEmail;

    private String nickname;

    private String onlineYn;

    private String socketSessionId;

    private LocalDateTime lastActiveAt;

    private LocalDateTime updatedAt;

    private String currentRoomKey;
}