package com.grape.module.chat.controller;

import com.grape.module.chat.model.request.ReadMessageRequest;
import com.grape.module.chat.service.ChatReadService;
import com.grape.security.auth.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/read")
public class ChatReadController {

    private final ChatReadService chatReadService;

    @PostMapping
    public void readMessage(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody ReadMessageRequest request){
        chatReadService.readMessage(user.getUserKey(), request);
    }
}
