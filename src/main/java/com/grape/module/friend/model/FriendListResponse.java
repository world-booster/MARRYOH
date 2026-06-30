package com.grape.module.friend.model;

import lombok.Data;

@Data
public class FriendListResponse {

    private String friendUserKey;
    private String friendNickname;
    private String friendEmail;
    private String onlineYn;
    private String currentRoomKey;
}
