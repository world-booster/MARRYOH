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
public class ChatEventLogDto {

    private String eventLogKey;

    private String roomKey;

    private String userKey;

    private String eventType;

    private String eventContent;

    private LocalDateTime createdAt;
}