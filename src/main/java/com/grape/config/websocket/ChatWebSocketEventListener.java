package com.grape.config.websocket;

import com.grape.module.chat.service.ChatPresenceService;
import com.grape.security.auth.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Log4j2
@Component
@RequiredArgsConstructor
public class ChatWebSocketEventListener {

    private final ChatPresenceService chatPresenceService; //TODO 임시로 사용 후에 포트로 변경
    private final SimpMessagingTemplate messagingTemplate;


    @EventListener
    public void handleConnect(SessionConnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        log.info("WS USER = {}", accessor.getUser());

//        Authentication authentication =
//                (Authentication) accessor.getUser();
//
//        if(authentication == null){
//            log.info("AUTH NULL");
//            return;
//        }
//
//        CustomUserPrincipal user =
//                (CustomUserPrincipal) authentication.getPrincipal();

        String userKey = (String)accessor.getSessionAttributes().get("userKey");
        String sessionId = accessor.getSessionId();

        if(userKey == null){
            return;
        }

        chatPresenceService.connect(userKey, sessionId);

        messagingTemplate.convertAndSend(
                "/subscribe/presence",
                chatPresenceService.getOnlineUsers()
        );

        log.info("웹소켓 연결 : {}", userKey);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event){
        String sessionId = event.getSessionId();
        chatPresenceService.disconnect(sessionId);
        messagingTemplate.convertAndSend(
                "/subscribe/presence",
                chatPresenceService.getOnlineUsers()
        );
        log.info("웹소켓 종료 : {}", sessionId);
    }
}
