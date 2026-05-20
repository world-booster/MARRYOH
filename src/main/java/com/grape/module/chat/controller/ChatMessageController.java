package com.grape.module.chat.controller;

import com.grape.module.chat.model.request.ChatMessageRequest;
import com.grape.module.chat.model.response.ChatMessageResponse;
import com.grape.module.chat.service.ChatMessageService;
import com.grape.security.auth.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/message")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /*
     * 메시지 전송
     */
    @PostMapping
    public ChatMessageResponse sendMessage(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @RequestBody ChatMessageRequest request
    ) {

        return chatMessageService.sendMessage(user.getUserKey(), request);
    }

    @GetMapping("/{roomKey}")
    public List<ChatMessageResponse> getMessageList(@PathVariable String roomKey){
        return chatMessageService.getMessageList(roomKey);
    }
}