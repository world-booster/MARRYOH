package com.grape.module.chat.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatTypingRequest {

    private String roomKey;

    private String userKey;

    /*
     * true : 입력중
     * false : 입력 종료
     */
    private Boolean typing;
}