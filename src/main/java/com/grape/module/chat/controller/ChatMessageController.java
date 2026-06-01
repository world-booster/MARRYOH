package com.grape.module.chat.controller;

import com.grape.module.chat.model.request.ChatMessageRequest;
import com.grape.module.chat.model.response.ChatMessageResponse;
import com.grape.module.chat.service.ChatMessageService;
import com.grape.module.chat.service.ChatPresenceService;
import com.grape.security.auth.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/message")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final ChatPresenceService chatPresenceService;

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
    public List<ChatMessageResponse> getMessageList(@PathVariable String roomKey, @AuthenticationPrincipal CustomUserPrincipal user){

        chatPresenceService.enterRoom(user.getUserKey(), roomKey);

        return chatMessageService.getMessageList(roomKey);
    }

    @GetMapping("/{roomKey}/history")
    public List<ChatMessageResponse> getHistory(@PathVariable String roomKey, @RequestParam LocalDateTime before){
        return chatMessageService.getHistory(roomKey, before);
    }
}