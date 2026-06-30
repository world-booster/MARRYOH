package com.grape.module.friend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendReqeustResponse {
        private String friendKey;
        private String requestUserKey;
        private String requestUserNickname;
        private String requestUserEmail;
        private LocalDateTime createdAt;
}
