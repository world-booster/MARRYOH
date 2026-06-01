package com.grape.config.websocket;

import com.grape.module.chat.service.ChatPresenceService;
import com.grape.security.auth.CustomUserPrincipal;
import com.grape.security.jwt.JwtAuthenticationProvider;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtAuthenticationProvider authenticationProvider; //TODO 추후 의존성 감소 고려

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT.equals(accessor.getCommand())){

            Object cookieObject = accessor.getSessionAttributes().get("cookies");

            if(cookieObject instanceof Cookie[] cookies){
                for(Cookie cookie : cookies){
                    if("ACCESS-TOKEN".equals(cookie.getName())){
                        Authentication authentication = authenticationProvider.authenticate(cookie.getValue());
                        accessor.setUser(authentication);
                        CustomUserPrincipal user = (CustomUserPrincipal) authentication.getPrincipal();
                        accessor.getSessionAttributes().put("userKey", user.getUserKey());
                        log.info("WS AUTH SUCCESS = {}", authentication.getName());
                        break;
                    }
                }
            }
        }

        return message;
    }
}
