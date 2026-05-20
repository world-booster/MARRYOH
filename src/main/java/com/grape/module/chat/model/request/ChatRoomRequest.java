package com.grape.module.chat.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequest {

    private String roomName;

    private String roomType;

    private List<String> userKeys;
}