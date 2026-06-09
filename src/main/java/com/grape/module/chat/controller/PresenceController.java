package com.grape.module.chat.controller;

import com.grape.module.chat.service.ChatPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PresenceController {

    private final ChatPresenceService chatPresenceService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/presence/sync")
    public void syncPresence(){

        messagingTemplate.convertAndSend(
                "/subscribe/presence",
                chatPresenceService.getAllPresenceUsers()
        );
    }
}