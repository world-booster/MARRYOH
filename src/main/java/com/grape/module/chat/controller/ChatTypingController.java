package com.grape.module.chat.controller;

import com.grape.module.chat.model.request.ChatTypingRequest;
import com.grape.module.chat.model.response.ChatTypingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatTypingController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/typing")
    public void typing(ChatTypingRequest request){
        ChatTypingResponse response = ChatTypingResponse.builder()
                .roomKey(request.getRoomKey())
                .userKey(request.getUserKey())
                .typing(request.getTyping())
                .build();

        messagingTemplate.convertAndSend("/subscribe/chat/typing/" + request.getRoomKey(), response);
    }
}
