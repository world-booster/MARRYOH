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
public class ChatRoomInviteLogDto {

    private String inviteLogKey;

    private String roomKey;

    private String inviteUserKey;

    private String invitedUserKey;

    private LocalDateTime createdAt;
}