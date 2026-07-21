package com.grape.module.chat.controller;

import com.grape.module.chat.model.request.ReadMessageRequest;
import com.grape.module.chat.service.ChatReadService;
import com.grape.security.auth.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/read")
public class ChatReadController {

    private final ChatReadService chatReadService;

    @ResponseBody
    @PostMapping
    public void readMessage(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody ReadMessageRequest request){
        chatReadService.readMessage(user.getUserKey(), request);
    }

//    @MessageMapping("/chat/read")
//    public void readMessageSocket(Authentication authentication, ReadMessageRequest request){
//        CustomUserPrincipal user = (CustomUserPrincipal) authentication.getPrincipal();
//
//        chatReadService.readMessage(user.getUserKey(), request);
//    }
}
