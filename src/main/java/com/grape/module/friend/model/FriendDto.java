package com.grape.module.friend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendDto {

    private String friendKey;
    private String requestUserKey;
    private String targetUserKey;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime updatedAt;
}
