package com.grape.module.chat.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatTypingResponse {

    private String roomKey;

    private String userKey;

    private Boolean typing;
}