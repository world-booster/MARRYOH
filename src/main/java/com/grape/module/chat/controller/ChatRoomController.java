package com.grape.module.chat.controller;

import com.grape.module.chat.model.request.ChatRoomRequest;
import com.grape.module.chat.model.response.ChatRoomResponse;
import com.grape.module.chat.service.ChatRoomService;
import com.grape.security.auth.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ChatRoomResponse createChatRoom(@AuthenticationPrincipal CustomUserPrincipal user, @RequestBody ChatRoomRequest request){
        ChatRoomResponse response = chatRoomService.createChatRoom(user.getUserKey(), request);
        messagingTemplate.convertAndSend("/topic/room", response);

        return response;
    }

    @GetMapping
    public List<ChatRoomResponse> getChatRoomList(@AuthenticationPrincipal CustomUserPrincipal user){
        return chatRoomService.getChatRoomList(user.getUserKey());
    }

    @PostMapping("/{roomKey}/invite")
    public ResponseEntity<Void> inviteUser(@PathVariable String roomKey, @RequestBody List<String> userKeys){
        chatRoomService.inviteUsers(roomKey, userKeys);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roomKey}")
    public ResponseEntity<Void> exitRoom(@PathVariable String roomKey, Authentication authentication){
        CustomUserPrincipal user = (CustomUserPrincipal) authentication.getPrincipal();

        chatRoomService.exitRoom(roomKey, user.getUserKey());

        return ResponseEntity.ok().build();
    }
}
